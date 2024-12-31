package net.minecraft.structure;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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
	protected Map<Long, GeneratorConfig> config = Maps.newHashMap();

	public abstract String getName();

	@Override
	protected final void carve(World world, int chunkX, int chunkZ, int mainChunkX, int mainChunkZ, ChunkBlockStateStorage chunkStorage) {
		this.method_5515(world);
		if (!this.config.containsKey(ChunkPos.getIdFromCoords(chunkX, chunkZ))) {
			this.random.nextInt();

			try {
				if (this.shouldStartAt(chunkX, chunkZ)) {
					GeneratorConfig generatorConfig = this.getGeneratorConfig(chunkX, chunkZ);
					this.config.put(ChunkPos.getIdFromCoords(chunkX, chunkZ), generatorConfig);
					this.method_5514(chunkX, chunkZ, generatorConfig);
				}
			} catch (Throwable var10) {
				CrashReport crashReport = CrashReport.create(var10, "Exception preparing structure feature");
				CrashReportSection crashReportSection = crashReport.addElement("Feature being prepared");
				crashReportSection.add("Is feature chunk", new Callable<String>() {
					public String call() throws Exception {
						return StructureFeature.this.shouldStartAt(chunkX, chunkZ) ? "True" : "False";
					}
				});
				crashReportSection.add("Chunk location", String.format("%d,%d", chunkX, chunkZ));
				crashReportSection.add("Chunk pos hash", new Callable<String>() {
					public String call() throws Exception {
						return String.valueOf(ChunkPos.getIdFromCoords(chunkX, chunkZ));
					}
				});
				crashReportSection.add("Structure type", new Callable<String>() {
					public String call() throws Exception {
						return StructureFeature.this.getClass().getCanonicalName();
					}
				});
				throw new CrashException(crashReport);
			}
		}
	}

	public boolean populate(World world, Random random, ChunkPos chunkPos) {
		this.method_5515(world);
		int i = (chunkPos.x << 4) + 8;
		int j = (chunkPos.z << 4) + 8;
		boolean bl = false;

		for (GeneratorConfig generatorConfig : this.config.values()) {
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

	protected GeneratorConfig getGeneratorConfigAtPos(BlockPos pos) {
		for (GeneratorConfig generatorConfig : this.config.values()) {
			if (generatorConfig.isValid() && generatorConfig.getBoundingBox().contains(pos)) {
				for (StructurePiece structurePiece : generatorConfig.getChildren()) {
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

		for (GeneratorConfig generatorConfig : this.config.values()) {
			if (generatorConfig.isValid() && generatorConfig.getBoundingBox().contains(pos)) {
				return true;
			}
		}

		return false;
	}

	public BlockPos method_9269(World world, BlockPos pos) {
		this.world = world;
		this.method_5515(world);
		this.random.setSeed(world.getSeed());
		long l = this.random.nextLong();
		long m = this.random.nextLong();
		long n = (long)(pos.getX() >> 4) * l;
		long o = (long)(pos.getZ() >> 4) * m;
		this.random.setSeed(n ^ o ^ world.getSeed());
		this.carve(world, pos.getX() >> 4, pos.getZ() >> 4, 0, 0, null);
		double d = Double.MAX_VALUE;
		BlockPos blockPos = null;

		for (GeneratorConfig generatorConfig : this.config.values()) {
			if (generatorConfig.isValid()) {
				StructurePiece structurePiece = (StructurePiece)generatorConfig.getChildren().get(0);
				BlockPos blockPos2 = structurePiece.getCenterBlockPos();
				double e = blockPos2.getSquaredDistance(pos);
				if (e < d) {
					d = e;
					blockPos = blockPos2;
				}
			}
		}

		if (blockPos != null) {
			return blockPos;
		} else {
			List<BlockPos> list = this.method_50();
			if (list != null) {
				BlockPos blockPos3 = null;

				for (BlockPos blockPos4 : list) {
					double f = blockPos4.getSquaredDistance(pos);
					if (f < d) {
						d = f;
						blockPos3 = blockPos4;
					}
				}

				return blockPos3;
			} else {
				return null;
			}
		}
	}

	protected List<BlockPos> method_50() {
		return null;
	}

	private void method_5515(World world) {
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
								this.config.put(ChunkPos.getIdFromCoords(i, j), generatorConfig);
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
}
