package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.class_3454;
import net.minecraft.class_3781;
import net.minecraft.class_3786;
import net.minecraft.class_4440;
import net.minecraft.class_4452;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ThreadExecutor;
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
	private final LongSet field_21837 = new LongOpenHashSet();
	private final ChunkGenerator<?> generator;
	private final ChunkStorage chunkWriter;
	private final Long2ObjectMap<Chunk> loadedChunksMap = Long2ObjectMaps.synchronize(new class_4440(8192));
	private Chunk field_21838;
	private final class_4452 field_21839;
	private final class_3454<ChunkPos, class_3786, ChunkBlockStateStorage> field_21840;
	private final ServerWorld world;
	private final ThreadExecutor field_21841;

	public ServerChunkProvider(ServerWorld serverWorld, ChunkStorage chunkStorage, ChunkGenerator<?> chunkGenerator, ThreadExecutor threadExecutor) {
		this.world = serverWorld;
		this.chunkWriter = chunkStorage;
		this.generator = chunkGenerator;
		this.field_21841 = threadExecutor;
		this.field_21839 = new class_4452(2, serverWorld, chunkGenerator, chunkStorage, threadExecutor);
		this.field_21840 = new class_3454<>(this.field_21839);
	}

	public Collection<Chunk> method_12772() {
		return this.loadedChunksMap.values();
	}

	public void unload(Chunk chunk) {
		if (this.world.dimension.method_17189(chunk.chunkX, chunk.chunkZ)) {
			this.field_21837.add(ChunkPos.getIdFromCoords(chunk.chunkX, chunk.chunkZ));
		}
	}

	public void unloadAll() {
		ObjectIterator var1 = this.loadedChunksMap.values().iterator();

		while (var1.hasNext()) {
			Chunk chunk = (Chunk)var1.next();
			this.unload(chunk);
		}
	}

	public void method_21248(int i, int j) {
		this.field_21837.remove(ChunkPos.getIdFromCoords(i, j));
	}

	@Nullable
	@Override
	public Chunk method_17044(int i, int j, boolean bl, boolean bl2) {
		Chunk chunk;
		synchronized (this.chunkWriter) {
			if (this.field_21838 != null && this.field_21838.method_3920().x == i && this.field_21838.method_3920().z == j) {
				return this.field_21838;
			}

			long l = ChunkPos.getIdFromCoords(i, j);
			chunk = (Chunk)this.loadedChunksMap.get(l);
			if (chunk != null) {
				this.field_21838 = chunk;
				return chunk;
			}

			if (bl) {
				try {
					chunk = this.chunkWriter.method_17186(this.world, i, j, chunkx -> {
						chunkx.method_9143(this.world.getLastUpdateTime());
						this.loadedChunksMap.put(ChunkPos.getIdFromCoords(i, j), chunkx);
					});
				} catch (Exception var12) {
					LOGGER.error("Couldn't load chunk", var12);
				}
			}
		}

		if (chunk != null) {
			this.field_21841.submit(chunk::loadToWorld);
			return chunk;
		} else if (bl2) {
			try {
				this.field_21840.method_15510();
				this.field_21840.method_15509(new ChunkPos(i, j));
				CompletableFuture<ChunkBlockStateStorage> completableFuture = this.field_21840.method_15511();
				return (Chunk)completableFuture.thenApply(this::method_21252).join();
			} catch (RuntimeException var11) {
				throw this.method_21250(i, j, var11);
			}
		} else {
			return null;
		}
	}

	@Override
	public class_3781 method_17043(int i, int j, boolean bl) {
		class_3781 lv = this.method_17044(i, j, true, false);
		return lv != null ? lv : this.field_21839.method_15495(new ChunkPos(i, j), bl);
	}

	public CompletableFuture<ChunkBlockStateStorage> method_21253(Iterable<ChunkPos> iterable, Consumer<Chunk> consumer) {
		this.field_21840.method_15510();

		for (ChunkPos chunkPos : iterable) {
			Chunk chunk = this.method_17044(chunkPos.x, chunkPos.z, true, false);
			if (chunk != null) {
				consumer.accept(chunk);
			} else {
				this.field_21840.method_15509(chunkPos).thenApply(this::method_21252).thenAccept(consumer);
			}
		}

		return this.field_21840.method_15511();
	}

	private CrashException method_21250(int i, int j, Throwable throwable) {
		CrashReport crashReport = CrashReport.create(throwable, "Exception generating new chunk");
		CrashReportSection crashReportSection = crashReport.addElement("Chunk to be generated");
		crashReportSection.add("Location", String.format("%d,%d", i, j));
		crashReportSection.add("Position hash", ChunkPos.getIdFromCoords(i, j));
		crashReportSection.add("Generator", this.generator);
		return new CrashException(crashReport);
	}

	private Chunk method_21252(class_3781 arg) {
		ChunkPos chunkPos = arg.method_3920();
		int i = chunkPos.x;
		int j = chunkPos.z;
		long l = ChunkPos.getIdFromCoords(i, j);
		Chunk chunk2;
		synchronized (this.loadedChunksMap) {
			Chunk chunk = (Chunk)this.loadedChunksMap.get(l);
			if (chunk != null) {
				return chunk;
			}

			if (arg instanceof Chunk) {
				chunk2 = (Chunk)arg;
			} else {
				if (!(arg instanceof ChunkBlockStateStorage)) {
					throw new IllegalStateException();
				}

				chunk2 = new Chunk(this.world, (ChunkBlockStateStorage)arg, i, j);
			}

			this.loadedChunksMap.put(l, chunk2);
			this.field_21838 = chunk2;
		}

		this.field_21841.submit(chunk2::loadToWorld);
		return chunk2;
	}

	private void method_21254(class_3781 arg) {
		try {
			arg.method_9143(this.world.getLastUpdateTime());
			this.chunkWriter.method_17185(this.world, arg);
		} catch (IOException var3) {
			LOGGER.error("Couldn't save chunk", var3);
		} catch (WorldSaveException var4) {
			LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", var4);
		}
	}

	public boolean saveAllChunks(boolean saveEntities) {
		int i = 0;
		this.field_21839.method_21308(() -> true);
		synchronized (this.chunkWriter) {
			ObjectIterator var4 = this.loadedChunksMap.values().iterator();

			while (var4.hasNext()) {
				Chunk chunk = (Chunk)var4.next();
				if (chunk.shouldSave(saveEntities)) {
					this.method_21254(chunk);
					chunk.setModified(false);
					if (++i == 24 && !saveEntities) {
						return false;
					}
				}
			}

			return true;
		}
	}

	@Override
	public void close() {
		try {
			this.field_21840.method_15508();
		} catch (InterruptedException var2) {
			LOGGER.error("Couldn't stop taskManager", var2);
		}
	}

	public void flushChunks() {
		synchronized (this.chunkWriter) {
			this.chunkWriter.save();
		}
	}

	@Override
	public boolean method_17045(BooleanSupplier booleanSupplier) {
		if (!this.world.savingDisabled) {
			if (!this.field_21837.isEmpty()) {
				Iterator<Long> iterator = this.field_21837.iterator();

				for (int i = 0; iterator.hasNext() && (booleanSupplier.getAsBoolean() || i < 200 || this.field_21837.size() > 2000); iterator.remove()) {
					Long long_ = (Long)iterator.next();
					synchronized (this.chunkWriter) {
						Chunk chunk = (Chunk)this.loadedChunksMap.get(long_);
						if (chunk != null) {
							chunk.unloadFromWorld();
							this.method_21254(chunk);
							this.loadedChunksMap.remove(long_);
							this.field_21838 = null;
							i++;
						}
					}
				}
			}

			this.field_21839.method_21308(booleanSupplier);
		}

		return false;
	}

	public boolean canSaveChunks() {
		return !this.world.savingDisabled;
	}

	@Override
	public String getChunkProviderName() {
		return "ServerChunkCache: " + this.loadedChunksMap.size() + " Drop: " + this.field_21837.size();
	}

	public List<Biome.SpawnEntry> method_12775(EntityCategory entityCategory, BlockPos blockPos) {
		return this.generator.getSpawnEntries(entityCategory, blockPos);
	}

	public int method_21251(World world, boolean bl, boolean bl2) {
		return this.generator.method_17014(world, bl, bl2);
	}

	@Nullable
	public BlockPos method_12773(World world, String string, BlockPos blockPos, int i, boolean bl) {
		return this.generator.method_3866(world, string, blockPos, i, bl);
	}

	@Override
	public ChunkGenerator<?> method_17046() {
		return this.generator;
	}

	public int method_3874() {
		return this.loadedChunksMap.size();
	}

	public boolean method_3864(int x, int z) {
		return this.loadedChunksMap.containsKey(ChunkPos.getIdFromCoords(x, z));
	}
}
