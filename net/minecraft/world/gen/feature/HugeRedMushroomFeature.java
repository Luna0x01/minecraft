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

public class HugeRedMushroomFeature extends Feature<PlantedFeatureConfig> {
	public HugeRedMushroomFeature(Function<Dynamic<?>, ? extends PlantedFeatureConfig> function) {
		super(function);
	}

	public boolean method_13398(
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

				for (int k = 0; k <= i; k++) {
					int l = 0;
					if (k < i && k >= i - 3) {
						l = 2;
					} else if (k == i) {
						l = 1;
					}

					for (int m = -l; m <= l; m++) {
						for (int n = -l; n <= l; n++) {
							BlockState blockState = iWorld.getBlockState(mutable.set(blockPos).setOffset(m, k, n));
							if (!blockState.isAir() && !blockState.matches(BlockTags.field_15503)) {
								return false;
							}
						}
					}
				}

				BlockState blockState2 = Blocks.field_10240.getDefaultState().with(MushroomBlock.DOWN, Boolean.valueOf(false));

				for (int o = i - 3; o <= i; o++) {
					int p = o < i ? 2 : 1;
					int q = 0;

					for (int r = -p; r <= p; r++) {
						for (int s = -p; s <= p; s++) {
							boolean bl = r == -p;
							boolean bl2 = r == p;
							boolean bl3 = s == -p;
							boolean bl4 = s == p;
							boolean bl5 = bl || bl2;
							boolean bl6 = bl3 || bl4;
							if (o >= i || bl5 != bl6) {
								mutable.set(blockPos).setOffset(r, o, s);
								if (!iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) {
									this.setBlockState(
										iWorld,
										mutable,
										blockState2.with(MushroomBlock.UP, Boolean.valueOf(o >= i - 1))
											.with(MushroomBlock.WEST, Boolean.valueOf(r < 0))
											.with(MushroomBlock.EAST, Boolean.valueOf(r > 0))
											.with(MushroomBlock.NORTH, Boolean.valueOf(s < 0))
											.with(MushroomBlock.SOUTH, Boolean.valueOf(s > 0))
									);
								}
							}
						}
					}
				}

				BlockState blockState3 = Blocks.field_10556
					.getDefaultState()
					.with(MushroomBlock.UP, Boolean.valueOf(false))
					.with(MushroomBlock.DOWN, Boolean.valueOf(false));

				for (int t = 0; t < i; t++) {
					mutable.set(blockPos).setOffset(Direction.field_11036, t);
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
