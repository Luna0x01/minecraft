package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class HugeBrownMushroomFeature extends Feature<PlantedFeatureConfig> {
	public HugeBrownMushroomFeature(Function<Dynamic<?>, ? extends PlantedFeatureConfig> function) {
		super(function);
	}

	public boolean method_13362(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, PlantedFeatureConfig plantedFeatureConfig
	) {
		int i = random.nextInt(3) + 4;
		if (random.nextInt(12) == 0) {
			i *= 2;
		}

		int j = blockPos.getY();
		if (j >= 1 && j + i + 1 < 256) {
			Block block = iWorld.getBlockState(blockPos.down()).getBlock();
			if (!Block.isNaturalDirt(block) && block != Blocks.field_10219 && block != Blocks.field_10402) {
				return false;
			} else {
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int k = 0; k <= 1 + i; k++) {
					int l = k <= 3 ? 0 : 3;

					for (int m = -l; m <= l; m++) {
						for (int n = -l; n <= l; n++) {
							BlockState blockState = iWorld.getBlockState(mutable.set(blockPos).setOffset(m, k, n));
							if (!blockState.isAir() && !blockState.matches(BlockTags.field_15503)) {
								return false;
							}
						}
					}
				}

				BlockState blockState2 = Blocks.field_10580
					.getDefaultState()
					.with(MushroomBlock.UP, Boolean.valueOf(true))
					.with(MushroomBlock.DOWN, Boolean.valueOf(false));
				int o = 3;

				for (int p = -3; p <= 3; p++) {
					for (int q = -3; q <= 3; q++) {
						boolean bl = p == -3;
						boolean bl2 = p == 3;
						boolean bl3 = q == -3;
						boolean bl4 = q == 3;
						boolean bl5 = bl || bl2;
						boolean bl6 = bl3 || bl4;
						if (!bl5 || !bl6) {
							mutable.set(blockPos).setOffset(p, i, q);
							if (!iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) {
								boolean bl7 = bl || bl6 && p == -2;
								boolean bl8 = bl2 || bl6 && p == 2;
								boolean bl9 = bl3 || bl5 && q == -2;
								boolean bl10 = bl4 || bl5 && q == 2;
								this.setBlockState(
									iWorld,
									mutable,
									blockState2.with(MushroomBlock.WEST, Boolean.valueOf(bl7))
										.with(MushroomBlock.EAST, Boolean.valueOf(bl8))
										.with(MushroomBlock.NORTH, Boolean.valueOf(bl9))
										.with(MushroomBlock.SOUTH, Boolean.valueOf(bl10))
								);
							}
						}
					}
				}

				BlockState blockState3 = Blocks.field_10556
					.getDefaultState()
					.with(MushroomBlock.UP, Boolean.valueOf(false))
					.with(MushroomBlock.DOWN, Boolean.valueOf(false));

				for (int r = 0; r < i; r++) {
					mutable.set(blockPos).setOffset(Direction.field_11036, r);
					if (!iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) {
						if (plantedFeatureConfig.planted) {
							iWorld.setBlockState(mutable, blockState3, 3);
						} else {
							this.setBlockState(iWorld, mutable, blockState3);
						}
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}
}
