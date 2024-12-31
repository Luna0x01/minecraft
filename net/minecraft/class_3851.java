package net.minecraft;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class class_3851 extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		int i = random.nextInt(3) + 4;
		if (random.nextInt(12) == 0) {
			i *= 2;
		}

		int j = blockPos.getY();
		if (j >= 1 && j + i + 1 < 256) {
			Block block = iWorld.getBlockState(blockPos.down()).getBlock();
			if (!Block.method_16588(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM) {
				return false;
			} else {
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int k = 0; k <= 1 + i; k++) {
					int l = k <= 3 ? 0 : 3;

					for (int m = -l; m <= l; m++) {
						for (int n = -l; n <= l; n++) {
							BlockState blockState = iWorld.getBlockState(mutable.set(blockPos).method_19934(m, k, n));
							if (!blockState.isAir() && !blockState.isIn(BlockTags.LEAVES)) {
								return false;
							}
						}
					}
				}

				BlockState blockState2 = Blocks.BROWN_MUSHROOM_BLOCK
					.getDefaultState()
					.withProperty(MushroomBlock.UP, Boolean.valueOf(true))
					.withProperty(MushroomBlock.DOWN, Boolean.valueOf(false));
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
							mutable.set(blockPos).method_19934(p, i, q);
							if (!iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) {
								boolean bl7 = bl || bl6 && p == -2;
								boolean bl8 = bl2 || bl6 && p == 2;
								boolean bl9 = bl3 || bl5 && q == -2;
								boolean bl10 = bl4 || bl5 && q == 2;
								this.method_17344(
									iWorld,
									mutable,
									blockState2.withProperty(MushroomBlock.WEST, Boolean.valueOf(bl7))
										.withProperty(MushroomBlock.EAST, Boolean.valueOf(bl8))
										.withProperty(MushroomBlock.NORTH, Boolean.valueOf(bl9))
										.withProperty(MushroomBlock.SOUTH, Boolean.valueOf(bl10))
								);
							}
						}
					}
				}

				BlockState blockState3 = Blocks.MUSHROOM_STEM
					.getDefaultState()
					.withProperty(MushroomBlock.UP, Boolean.valueOf(false))
					.withProperty(MushroomBlock.DOWN, Boolean.valueOf(false));

				for (int r = 0; r < i; r++) {
					mutable.set(blockPos).move(Direction.UP, r);
					if (!iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) {
						this.method_17344(iWorld, mutable, blockState3);
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}
}
