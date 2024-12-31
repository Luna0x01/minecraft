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

public class class_3852 extends class_3844<class_3871> {
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

				for (int k = 0; k <= i; k++) {
					int l = 0;
					if (k < i && k >= i - 3) {
						l = 2;
					} else if (k == i) {
						l = 1;
					}

					for (int m = -l; m <= l; m++) {
						for (int n = -l; n <= l; n++) {
							BlockState blockState = iWorld.getBlockState(mutable.set(blockPos).method_19934(m, k, n));
							if (!blockState.isAir() && !blockState.isIn(BlockTags.LEAVES)) {
								return false;
							}
						}
					}
				}

				BlockState blockState2 = Blocks.RED_MUSHROOM_BLOCK.getDefaultState().withProperty(MushroomBlock.DOWN, Boolean.valueOf(false));

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
								mutable.set(blockPos).method_19934(r, o, s);
								if (!iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) {
									this.method_17344(
										iWorld,
										mutable,
										blockState2.withProperty(MushroomBlock.UP, Boolean.valueOf(o >= i - 1))
											.withProperty(MushroomBlock.WEST, Boolean.valueOf(r < 0))
											.withProperty(MushroomBlock.EAST, Boolean.valueOf(r > 0))
											.withProperty(MushroomBlock.NORTH, Boolean.valueOf(s < 0))
											.withProperty(MushroomBlock.SOUTH, Boolean.valueOf(s > 0))
									);
								}
							}
						}
					}
				}

				BlockState blockState3 = Blocks.MUSHROOM_STEM
					.getDefaultState()
					.withProperty(MushroomBlock.UP, Boolean.valueOf(false))
					.withProperty(MushroomBlock.DOWN, Boolean.valueOf(false));

				for (int t = 0; t < i; t++) {
					mutable.set(blockPos).move(Direction.UP, t);
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
