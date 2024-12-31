package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.collection.LongObjectStorage;
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
	private Set<Long> chunksToUnload = Collections.newSetFromMap(new ConcurrentHashMap());
	private Chunk empty;
	private ChunkProvider chunkGenerator;
	private ChunkStorage chunkWriter;
	public boolean canGenerateChunks = true;
	private LongObjectStorage<Chunk> chunkStorage = new LongObjectStorage<>();
	private List<Chunk> chunks = Lists.newArrayList();
	private ServerWorld world;

	public ServerChunkProvider(ServerWorld serverWorld, ChunkStorage chunkStorage, ChunkProvider chunkProvider) {
		this.empty = new EmptyChunk(serverWorld, 0, 0);
		this.world = serverWorld;
		this.chunkWriter = chunkStorage;
		this.chunkGenerator = chunkProvider;
	}

	@Override
	public boolean chunkExists(int x, int z) {
		return this.chunkStorage.contains(ChunkPos.getIdFromCoords(x, z));
	}

	public List<Chunk> getChunks() {
		return this.chunks;
	}

	public void scheduleUnload(int y, int z) {
		if (this.world.dimension.containsWorldSpawn()) {
			if (!this.world.isChunkInsideSpawnChunks(y, z)) {
				this.chunksToUnload.add(ChunkPos.getIdFromCoords(y, z));
			}
		} else {
			this.chunksToUnload.add(ChunkPos.getIdFromCoords(y, z));
		}
	}

	public void unloadAll() {
		for (Chunk chunk : this.chunks) {
			this.scheduleUnload(chunk.chunkX, chunk.chunkZ);
		}
	}

	public Chunk getOrGenerateChunk(int x, int z) {
		long l = ChunkPos.getIdFromCoords(x, z);
		this.chunksToUnload.remove(l);
		Chunk chunk = this.chunkStorage.get(l);
		if (chunk == null) {
			chunk = this.loadChunk(x, z);
			if (chunk == null) {
				if (this.chunkGenerator == null) {
					chunk = this.empty;
				} else {
					try {
						chunk = this.chunkGenerator.getChunk(x, z);
					} catch (Throwable var9) {
						CrashReport crashReport = CrashReport.create(var9, "Exception generating new chunk");
						CrashReportSection crashReportSection = crashReport.addElement("Chunk to be generated");
						crashReportSection.add("Location", String.format("%d,%d", x, z));
						crashReportSection.add("Position hash", l);
						crashReportSection.add("Generator", this.chunkGenerator.getChunkProviderName());
						throw new CrashException(crashReport);
					}
				}
			}

			this.chunkStorage.set(l, chunk);
			this.chunks.add(chunk);
			chunk.loadToWorld();
			chunk.decorateChunk(this, this, x, z);
		}

		return chunk;
	}

	@Override
	public Chunk getChunk(int x, int z) {
		Chunk chunk = this.chunkStorage.get(ChunkPos.getIdFromCoords(x, z));
		if (chunk == null) {
			return !this.world.method_8522() && !this.canGenerateChunks ? this.empty : this.getOrGenerateChunk(x, z);
		} else {
			return chunk;
		}
	}

	private Chunk loadChunk(int x, int z) {
		if (this.chunkWriter == null) {
			return null;
		} else {
			try {
				Chunk chunk = this.chunkWriter.loadChunk(this.world, x, z);
				if (chunk != null) {
					chunk.setLastSaveTime(this.world.getLastUpdateTime());
					if (this.chunkGenerator != null) {
						this.chunkGenerator.handleInitialLoad(chunk, x, z);
					}
				}

				return chunk;
			} catch (Exception var4) {
				LOGGER.error("Couldn't load chunk", var4);
				return null;
			}
		}
	}

	private void saveEntities(Chunk chunk) {
		if (this.chunkWriter != null) {
			try {
				this.chunkWriter.writeEntities(this.world, chunk);
			} catch (Exception var3) {
				LOGGER.error("Couldn't save entities", var3);
			}
		}
	}

	private void saveChunk(Chunk chunk) {
		if (this.chunkWriter != null) {
			try {
				chunk.setLastSaveTime(this.world.getLastUpdateTime());
				this.chunkWriter.writeChunk(this.world, chunk);
			} catch (IOException var3) {
				LOGGER.error("Couldn't save chunk", var3);
			} catch (WorldSaveException var4) {
				LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", var4);
			}
		}
	}

	@Override
	public void decorateChunk(ChunkProvider provider, int x, int z) {
		Chunk chunk = this.getChunk(x, z);
		if (!chunk.isTerrainPopulated()) {
			chunk.populate();
			if (this.chunkGenerator != null) {
				this.chunkGenerator.decorateChunk(provider, x, z);
				chunk.setModified();
			}
		}
	}

	@Override
	public boolean isChunkModified(ChunkProvider chunkProvider, Chunk chunk, int x, int z) {
		if (this.chunkGenerator != null && this.chunkGenerator.isChunkModified(chunkProvider, chunk, x, z)) {
			Chunk chunk2 = this.getChunk(x, z);
			chunk2.setModified();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean saveChunks(boolean saveEntities, ProgressListener progressListener) {
		int i = 0;
		List<Chunk> list = Lists.newArrayList(this.chunks);

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

	@Override
	public void flushChunks() {
		if (this.chunkWriter != null) {
			this.chunkWriter.save();
		}
	}

	@Override
	public boolean tickChunks() {
		if (!this.world.savingDisabled) {
			for (int i = 0; i < 100; i++) {
				if (!this.chunksToUnload.isEmpty()) {
					Long long_ = (Long)this.chunksToUnload.iterator().next();
					Chunk chunk = this.chunkStorage.get(long_);
					if (chunk != null) {
						chunk.unloadFromWorld();
						this.saveChunk(chunk);
						this.saveEntities(chunk);
						this.chunkStorage.remove(long_);
						this.chunks.remove(chunk);
					}

					this.chunksToUnload.remove(long_);
				}
			}

			if (this.chunkWriter != null) {
				this.chunkWriter.method_3950();
			}
		}

		return this.chunkGenerator.tickChunks();
	}

	@Override
	public boolean isSavingEnabled() {
		return !this.world.savingDisabled;
	}

	@Override
	public String getChunkProviderName() {
		return "ServerChunkCache: " + this.chunkStorage.getUsedEntriesCount() + " Drop: " + this.chunksToUnload.size();
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		return this.chunkGenerator.getSpawnEntries(category, pos);
	}

	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos) {
		return this.chunkGenerator.getNearestStructurePos(world, structureName, pos);
	}

	@Override
	public int getLoadedChunksCount() {
		return this.chunkStorage.getUsedEntriesCount();
	}

	@Override
	public void handleInitialLoad(Chunk chunk, int x, int z) {
	}

	@Override
	public Chunk getChunk(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}
}
