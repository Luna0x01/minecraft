package net.minecraft;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class class_3865 extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		if (!iWorld.method_8579(blockPos)) {
			return false;
		} else if (iWorld.getBlockState(blockPos.up()).getBlock() != Blocks.NETHERRACK) {
			return false;
		} else {
			iWorld.setBlockState(blockPos, Blocks.GLOWSTONE.getDefaultState(), 2);

			for (int i = 0; i < 1500; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
				if (iWorld.getBlockState(blockPos2).isAir()) {
					int j = 0;

					for (Direction direction : Direction.values()) {
						if (iWorld.getBlockState(blockPos2.offset(direction)).getBlock() == Blocks.GLOWSTONE) {
							j++;
						}

						if (j > 1) {
							break;
						}
					}

					if (j == 1) {
						iWorld.setBlockState(blockPos2, Blocks.GLOWSTONE.getDefaultState(), 2);
					}
				}
			}

			return true;
		}
	}
}
