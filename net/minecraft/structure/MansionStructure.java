package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.GeneratorConfig;

public class MansionStructure extends StructureFeature {
	private final int field_15193 = 80;
	private final int field_15194 = 20;
	public static final List<Biome> field_15192 = Arrays.asList(Biomes.ROOFED_FOREST, Biomes.ROOFED_FOREST_M);
	private final SurfaceChunkGenerator field_15195;

	public MansionStructure(SurfaceChunkGenerator surfaceChunkGenerator) {
		this.field_15195 = surfaceChunkGenerator;
	}

	@Override
	public String getName() {
		return "Mansion";
	}

	@Override
	protected boolean shouldStartAt(int chunkX, int chunkZ) {
		int i = chunkX;
		int j = chunkZ;
		if (chunkX < 0) {
			i = chunkX - 79;
		}

		if (chunkZ < 0) {
			j = chunkZ - 79;
		}

		int k = i / 80;
		int l = j / 80;
		Random random = this.world.getStructureRandom(k, l, 10387319);
		k *= 80;
		l *= 80;
		k += (random.nextInt(60) + random.nextInt(60)) / 2;
		l += (random.nextInt(60) + random.nextInt(60)) / 2;
		if (chunkX == k && chunkZ == l) {
			boolean bl = this.world.method_3726().method_3854(chunkX * 16 + 8, chunkZ * 16 + 8, 32, field_15192);
			if (bl) {
				return true;
			}
		}

		return false;
	}

	@Override
	public BlockPos method_9269(World world, BlockPos blockPos, boolean bl) {
		this.world = world;
		SingletonBiomeSource singletonBiomeSource = world.method_3726();
		return singletonBiomeSource.method_13697() && singletonBiomeSource.method_13698() != Biomes.ROOFED_FOREST
			? null
			: method_13774(world, this, blockPos, 80, 20, 10387319, true, 100, bl);
	}

	@Override
	protected GeneratorConfig getGeneratorConfig(int chunkX, int chunkZ) {
		return new MansionStructure.MansionGeneratorConfig(this.world, this.field_15195, this.random, chunkX, chunkZ);
	}

	public static class MansionGeneratorConfig extends GeneratorConfig {
		private boolean field_15196;

		public MansionGeneratorConfig() {
		}

		public MansionGeneratorConfig(World world, SurfaceChunkGenerator surfaceChunkGenerator, Random random, int i, int j) {
			super(i, j);
			this.method_13776(world, surfaceChunkGenerator, random, i, j);
		}

		private void method_13776(World world, SurfaceChunkGenerator surfaceChunkGenerator, Random random, int i, int j) {
			BlockRotation blockRotation = BlockRotation.values()[random.nextInt(BlockRotation.values().length)];
			ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage();
			surfaceChunkGenerator.method_9194(i, j, chunkBlockStateStorage);
			int k = 5;
			int l = 5;
			if (blockRotation == BlockRotation.CLOCKWISE_90) {
				k = -5;
			} else if (blockRotation == BlockRotation.CLOCKWISE_180) {
				k = -5;
				l = -5;
			} else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
				l = -5;
			}

			int m = chunkBlockStateStorage.method_11819(7, 7);
			int n = chunkBlockStateStorage.method_11819(7, 7 + l);
			int o = chunkBlockStateStorage.method_11819(7 + k, 7);
			int p = chunkBlockStateStorage.method_11819(7 + k, 7 + l);
			int q = Math.min(Math.min(m, n), Math.min(o, p));
			if (q < 60) {
				this.field_15196 = false;
			} else {
				BlockPos blockPos = new BlockPos(i * 16 + 8, q + 1, j * 16 + 8);
				List<class_3072.class_3081> list = Lists.newLinkedList();
				class_3072.method_13778(world.getSaveHandler().method_11956(), blockPos, blockRotation, list, random);
				this.field_13015.addAll(list);
				this.setBoundingBoxFromChildren();
				this.field_15196 = true;
			}
		}

		@Override
		public void generateStructure(World world, Random random, BlockBox boundingBox) {
			super.generateStructure(world, random, boundingBox);
			int i = this.boundingBox.minY;

			for (int j = boundingBox.minX; j <= boundingBox.maxX; j++) {
				for (int k = boundingBox.minZ; k <= boundingBox.maxZ; k++) {
					BlockPos blockPos = new BlockPos(j, i, k);
					if (!world.isAir(blockPos) && this.boundingBox.contains(blockPos)) {
						boolean bl = false;

						for (StructurePiece structurePiece : this.field_13015) {
							if (structurePiece.boundingBox.contains(blockPos)) {
								bl = true;
								break;
							}
						}

						if (bl) {
							for (int l = i - 1; l > 1; l--) {
								BlockPos blockPos2 = new BlockPos(j, l, k);
								if (!world.isAir(blockPos2) && !world.getBlockState(blockPos2).getMaterial().isFluid()) {
									break;
								}

								world.setBlockState(blockPos2, Blocks.COBBLESTONE.getDefaultState(), 2);
							}
						}
					}
				}
			}
		}

		@Override
		public boolean isValid() {
			return this.field_15196;
		}
	}
}
