package net.minecraft;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3913 extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		BlockPos blockPos2 = iWorld.method_3585();
		int i = 16;
		double d = blockPos2.getSquaredDistance(blockPos.add(8, blockPos2.getY(), 8));
		if (d > 1024.0) {
			return true;
		} else {
			BlockPos blockPos3 = new BlockPos(blockPos2.getX() - 16, Math.max(blockPos2.getY(), 4) - 1, blockPos2.getZ() - 16);
			BlockPos blockPos4 = new BlockPos(blockPos2.getX() + 16, Math.max(blockPos2.getY(), 4) - 1, blockPos2.getZ() + 16);
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos3);

			for (int j = blockPos.getZ(); j < blockPos.getZ() + 16; j++) {
				for (int k = blockPos.getX(); k < blockPos.getX() + 16; k++) {
					if (j >= blockPos3.getZ() && j <= blockPos4.getZ() && k >= blockPos3.getX() && k <= blockPos4.getX()) {
						mutable.setPosition(k, mutable.getY(), j);
						if (blockPos2.getX() == k && blockPos2.getZ() == j) {
							iWorld.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), 2);
						} else {
							iWorld.setBlockState(mutable, Blocks.STONE.getDefaultState(), 2);
						}
					}
				}
			}

			return true;
		}
	}
}
