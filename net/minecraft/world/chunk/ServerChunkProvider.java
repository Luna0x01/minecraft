package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.storage.WorldSaveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerChunkProvider implements ChunkProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Set<Long> chunksToUnload = Sets.newHashSet();
	private final ChunkGenerator generator;
	private final ChunkStorage chunkWriter;
	private final Long2ObjectMap<Chunk> loadedChunksMap = new Long2ObjectOpenHashMap(8192);
	private final ServerWorld world;

	public ServerChunkProvider(ServerWorld serverWorld, ChunkStorage chunkStorage, ChunkGenerator chunkGenerator) {
		this.world = serverWorld;
		this.chunkWriter = chunkStorage;
		this.generator = chunkGenerator;
	}

	public Collection<Chunk> method_12772() {
		return this.loadedChunksMap.values();
	}

	public void unload(Chunk chunk) {
		if (this.world.dimension.canChunkBeUnloaded(chunk.chunkX, chunk.chunkZ)) {
			this.chunksToUnload.add(ChunkPos.getIdFromCoords(chunk.chunkX, chunk.chunkZ));
			chunk.unloaded = true;
		}
	}

	public void unloadAll() {
		ObjectIterator var1 = this.loadedChunksMap.values().iterator();

		while (var1.hasNext()) {
			Chunk chunk = (Chunk)var1.next();
			this.unload(chunk);
		}
	}

	@Nullable
	@Override
	public Chunk getLoadedChunk(int x, int z) {
		long l = ChunkPos.getIdFromCoords(x, z);
		Chunk chunk = (Chunk)this.loadedChunksMap.get(l);
		if (chunk != null) {
			chunk.unloaded = false;
		}

		return chunk;
	}

	@Nullable
	public Chunk getOrLoadChunk(int x, int z) {
		Chunk chunk = this.getLoadedChunk(x, z);
		if (chunk == null) {
			chunk = this.loadChunk(x, z);
			if (chunk != null) {
				this.loadedChunksMap.put(ChunkPos.getIdFromCoords(x, z), chunk);
				chunk.loadToWorld();
				chunk.populateIfMissing(this, this.generator);
			}
		}

		return chunk;
	}

	@Override
	public Chunk getOrGenerateChunks(int x, int z) {
		Chunk chunk = this.getOrLoadChunk(x, z);
		if (chunk == null) {
			long l = ChunkPos.getIdFromCoords(x, z);

			try {
				chunk = this.generator.generate(x, z);
			} catch (Throwable var9) {
				CrashReport crashReport = CrashReport.create(var9, "Exception generating new chunk");
				CrashReportSection crashReportSection = crashReport.addElement("Chunk to be generated");
				crashReportSection.add("Location", String.format("%d,%d", x, z));
				crashReportSection.add("Position hash", l);
				crashReportSection.add("Generator", this.generator);
				throw new CrashException(crashReport);
			}

			this.loadedChunksMap.put(l, chunk);
			chunk.loadToWorld();
			chunk.populateIfMissing(this, this.generator);
		}

		return chunk;
	}

	@Nullable
	private Chunk loadChunk(int x, int z) {
		try {
			Chunk chunk = this.chunkWriter.loadChunk(this.world, x, z);
			if (chunk != null) {
				chunk.setLastSaveTime(this.world.getLastUpdateTime());
				this.generator.method_4702(chunk, x, z);
			}

			return chunk;
		} catch (Exception var4) {
			LOGGER.error("Couldn't load chunk", var4);
			return null;
		}
	}

	private void saveEntities(Chunk chunk) {
		try {
			this.chunkWriter.writeEntities(this.world, chunk);
		} catch (Exception var3) {
			LOGGER.error("Couldn't save entities", var3);
		}
	}

	private void saveChunk(Chunk chunk) {
		try {
			chunk.setLastSaveTime(this.world.getLastUpdateTime());
			this.chunkWriter.writeChunk(this.world, chunk);
		} catch (IOException var3) {
			LOGGER.error("Couldn't save chunk", var3);
		} catch (WorldSaveException var4) {
			LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", var4);
		}
	}

	public boolean saveAllChunks(boolean saveEntities) {
		int i = 0;
		List<Chunk> list = Lists.newArrayList(this.loadedChunksMap.values());

		for (int j = 0; j < list.size(); j++) {
			Chunk chunk = (Chunk)list.get(j);
			if (saveEntities) {
				this.saveEntities(chunk);
			}

			if (chunk.shouldSave(saveEntities)) {
				this.saveChunk(chunk);
				chunk.setModified(false);
				if (++i == 24 && !saveEntities) {
					return false;
				}
			}
		}

		return true;
	}

	public void flushChunks() {
		this.chunkWriter.save();
	}

	@Override
	public boolean tickChunks() {
		if (!this.world.savingDisabled) {
			if (!this.chunksToUnload.isEmpty()) {
				Iterator<Long> iterator = this.chunksToUnload.iterator();

				for (int i = 0; i < 100 && iterator.hasNext(); iterator.remove()) {
					Long long_ = (Long)iterator.next();
					Chunk chunk = (Chunk)this.loadedChunksMap.get(long_);
					if (chunk != null && chunk.unloaded) {
						chunk.unloadFromWorld();
						this.saveChunk(chunk);
						this.saveEntities(chunk);
						this.loadedChunksMap.remove(long_);
						i++;
					}
				}
			}

			this.chunkWriter.method_3950();
		}

		return false;
	}

	public boolean canSaveChunks() {
		return !this.world.savingDisabled;
	}

	@Override
	public String getChunkProviderName() {
		return "ServerChunkCache: " + this.loadedChunksMap.size() + " Drop: " + this.chunksToUnload.size();
	}

	public List<Biome.SpawnEntry> method_12775(EntityCategory entityCategory, BlockPos blockPos) {
		return this.generator.getSpawnEntries(entityCategory, blockPos);
	}

	@Nullable
	public BlockPos method_12773(World world, String string, BlockPos pos, boolean bl) {
		return this.generator.method_3866(world, string, pos, bl);
	}

	public boolean method_14961(World world, String string, BlockPos pos) {
		return this.generator.method_14387(world, string, pos);
	}

	public int method_3874() {
		return this.loadedChunksMap.size();
	}

	public boolean method_3864(int x, int z) {
		return this.loadedChunksMap.containsKey(ChunkPos.getIdFromCoords(x, z));
	}

	@Override
	public boolean isChunkGenerated(int x, int z) {
		return this.loadedChunksMap.containsKey(ChunkPos.getIdFromCoords(x, z)) || this.chunkWriter.chunkExists(x, z);
	}
}
