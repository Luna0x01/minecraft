package net.minecraft.structure;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.gen.GeneratorConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.feature.FeatureState;

public abstract class StructureFeature extends Carver {
	private FeatureState field_6237;
	protected Long2ObjectMap<GeneratorConfig> field_13012 = new Long2ObjectOpenHashMap(1024);

	public abstract String getName();

	@Override
	protected final synchronized void carve(World world, int chunkX, int chunkZ, int mainChunkX, int mainChunkZ, ChunkBlockStateStorage chunkStorage) {
		this.method_5515(world);
		if (!this.field_13012.containsKey(ChunkPos.getIdFromCoords(chunkX, chunkZ))) {
			this.random.nextInt();

			try {
				if (this.shouldStartAt(chunkX, chunkZ)) {
					GeneratorConfig generatorConfig = this.getGeneratorConfig(chunkX, chunkZ);
					this.field_13012.put(ChunkPos.getIdFromCoords(chunkX, chunkZ), generatorConfig);
					if (generatorConfig.isValid()) {
						this.method_5514(chunkX, chunkZ, generatorConfig);
					}
				}
			} catch (Throwable var10) {
				CrashReport crashReport = CrashReport.create(var10, "Exception preparing structure feature");
				CrashReportSection crashReportSection = crashReport.addElement("Feature being prepared");
				crashReportSection.add("Is feature chunk", new CrashCallable<String>() {
					public String call() throws Exception {
						return StructureFeature.this.shouldStartAt(chunkX, chunkZ) ? "True" : "False";
					}
				});
				crashReportSection.add("Chunk location", String.format("%d,%d", chunkX, chunkZ));
				crashReportSection.add("Chunk pos hash", new CrashCallable<String>() {
					public String call() throws Exception {
						return String.valueOf(ChunkPos.getIdFromCoords(chunkX, chunkZ));
					}
				});
				crashReportSection.add("Structure type", new CrashCallable<String>() {
					public String call() throws Exception {
						return StructureFeature.this.getClass().getCanonicalName();
					}
				});
				throw new CrashException(crashReport);
			}
		}
	}

	public synchronized boolean populate(World world, Random random, ChunkPos chunkPos) {
		this.method_5515(world);
		int i = (chunkPos.x << 4) + 8;
		int j = (chunkPos.z << 4) + 8;
		boolean bl = false;
		ObjectIterator var7 = this.field_13012.values().iterator();

		while (var7.hasNext()) {
			GeneratorConfig generatorConfig = (GeneratorConfig)var7.next();
			if (generatorConfig.isValid() && generatorConfig.method_9277(chunkPos) && generatorConfig.getBoundingBox().intersectsXZ(i, j, i + 15, j + 15)) {
				generatorConfig.generateStructure(world, random, new BlockBox(i, j, i + 15, j + 15));
				generatorConfig.method_9278(chunkPos);
				bl = true;
				this.method_5514(generatorConfig.getChunkX(), generatorConfig.getChunkZ(), generatorConfig);
			}
		}

		return bl;
	}

	public boolean method_9270(BlockPos pos) {
		this.method_5515(this.world);
		return this.getGeneratorConfigAtPos(pos) != null;
	}

	@Nullable
	protected GeneratorConfig getGeneratorConfigAtPos(BlockPos pos) {
		ObjectIterator var2 = this.field_13012.values().iterator();

		while (var2.hasNext()) {
			GeneratorConfig generatorConfig = (GeneratorConfig)var2.next();
			if (generatorConfig.isValid() && generatorConfig.getBoundingBox().contains(pos)) {
				for (StructurePiece structurePiece : generatorConfig.method_11855()) {
					if (structurePiece.getBoundingBox().contains(pos)) {
						return generatorConfig;
					}
				}
			}
		}

		return null;
	}

	public boolean method_9267(World world, BlockPos pos) {
		this.method_5515(world);
		ObjectIterator var3 = this.field_13012.values().iterator();

		while (var3.hasNext()) {
			GeneratorConfig generatorConfig = (GeneratorConfig)var3.next();
			if (generatorConfig.isValid() && generatorConfig.getBoundingBox().contains(pos)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	public abstract BlockPos method_9269(World world, BlockPos blockPos, boolean bl);

	protected void method_5515(World world) {
		if (this.field_6237 == null) {
			this.field_6237 = (FeatureState)world.getOrCreateState(FeatureState.class, this.getName());
			if (this.field_6237 == null) {
				this.field_6237 = new FeatureState(this.getName());
				world.replaceState(this.getName(), this.field_6237);
			} else {
				NbtCompound nbtCompound = this.field_6237.getFeatures();

				for (String string : nbtCompound.getKeys()) {
					NbtElement nbtElement = nbtCompound.get(string);
					if (nbtElement.getType() == 10) {
						NbtCompound nbtCompound2 = (NbtCompound)nbtElement;
						if (nbtCompound2.contains("ChunkX") && nbtCompound2.contains("ChunkZ")) {
							int i = nbtCompound2.getInt("ChunkX");
							int j = nbtCompound2.getInt("ChunkZ");
							GeneratorConfig generatorConfig = StructurePieceManager.getGeneratorConfigFromNbt(nbtCompound2, world);
							if (generatorConfig != null) {
								this.field_13012.put(ChunkPos.getIdFromCoords(i, j), generatorConfig);
							}
						}
					}
				}
			}
		}
	}

	private void method_5514(int i, int j, GeneratorConfig generatorConfig) {
		this.field_6237.putFeature(generatorConfig.toNbt(i, j), i, j);
		this.field_6237.markDirty();
	}

	protected abstract boolean shouldStartAt(int chunkX, int chunkZ);

	protected abstract GeneratorConfig getGeneratorConfig(int chunkX, int chunkZ);

	protected static BlockPos method_13774(World world, StructureFeature structureFeature, BlockPos blockPos, int i, int j, int k, boolean bl, int l, boolean bl2) {
		int m = blockPos.getX() >> 4;
		int n = blockPos.getZ() >> 4;
		int o = 0;

		for (Random random = new Random(); o <= l; o++) {
			for (int p = -o; p <= o; p++) {
				boolean bl3 = p == -o || p == o;

				for (int q = -o; q <= o; q++) {
					boolean bl4 = q == -o || q == o;
					if (bl3 || bl4) {
						int r = m + i * p;
						int s = n + i * q;
						if (r < 0) {
							r -= i - 1;
						}

						if (s < 0) {
							s -= i - 1;
						}

						int t = r / i;
						int u = s / i;
						Random random2 = world.getStructureRandom(t, u, k);
						t *= i;
						u *= i;
						if (bl) {
							t += (random2.nextInt(i - j) + random2.nextInt(i - j)) / 2;
							u += (random2.nextInt(i - j) + random2.nextInt(i - j)) / 2;
						} else {
							t += random2.nextInt(i - j);
							u += random2.nextInt(i - j);
						}

						Carver.method_13769(world.getSeed(), random, t, u);
						random.nextInt();
						if (structureFeature.shouldStartAt(t, u)) {
							if (!bl2 || !world.method_13690(t, u)) {
								return new BlockPos((t << 4) + 8, 64, (u << 4) + 8);
							}
						} else if (o == 0) {
							break;
						}
					}
				}

				if (o == 0) {
					break;
				}
			}
		}

		return null;
	}
}
