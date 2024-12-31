package net.minecraft;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class class_3814 extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		if (blockPos.getY() > iWorld.method_8483() - 1) {
			return false;
		} else if (iWorld.getBlockState(blockPos).getBlock() != Blocks.WATER && iWorld.getBlockState(blockPos.down()).getBlock() != Blocks.WATER) {
			return false;
		} else {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.DOWN && iWorld.getBlockState(blockPos.offset(direction)).getBlock() == Blocks.PACKED_ICE) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				return false;
			} else {
				iWorld.setBlockState(blockPos, Blocks.BLUE_ICE.getDefaultState(), 2);

				for (int i = 0; i < 200; i++) {
					int j = random.nextInt(5) - random.nextInt(6);
					int k = 3;
					if (j < 2) {
						k += j / 2;
					}

					if (k >= 1) {
						BlockPos blockPos2 = blockPos.add(random.nextInt(k) - random.nextInt(k), j, random.nextInt(k) - random.nextInt(k));
						BlockState blockState = iWorld.getBlockState(blockPos2);
						Block block = blockState.getBlock();
						if (blockState.getMaterial() == Material.AIR || block == Blocks.WATER || block == Blocks.PACKED_ICE || block == Blocks.ICE) {
							for (Direction direction2 : Direction.values()) {
								Block block2 = iWorld.getBlockState(blockPos2.offset(direction2)).getBlock();
								if (block2 == Blocks.BLUE_ICE) {
									iWorld.setBlockState(blockPos2, Blocks.BLUE_ICE.getDefaultState(), 2);
									break;
								}
							}
						}
					}
				}

				return true;
			}
		}
	}
}
