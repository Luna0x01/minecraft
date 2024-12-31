package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3813;
import net.minecraft.class_3844;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class BlockFeature extends class_3844<class_3813> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3813 arg) {
		while (blockPos.getY() > 3) {
			if (!iWorld.method_8579(blockPos.down())) {
				Block block = iWorld.getBlockState(blockPos.down()).getBlock();
				if (block == Blocks.GRASS_BLOCK || Block.method_16588(block) || Block.method_16585(block)) {
					break;
				}
			}

			blockPos = blockPos.down();
		}

		if (blockPos.getY() <= 3) {
			return false;
		} else {
			int i = arg.field_19071;

			for (int j = 0; i >= 0 && j < 3; j++) {
				int k = i + random.nextInt(2);
				int l = i + random.nextInt(2);
				int m = i + random.nextInt(2);
				float f = (float)(k + l + m) * 0.333F + 0.5F;

				for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-k, -l, -m), blockPos.add(k, l, m))) {
					if (blockPos2.getSquaredDistance(blockPos) <= (double)(f * f)) {
						iWorld.setBlockState(blockPos2, arg.field_19070.getDefaultState(), 4);
					}
				}

				blockPos = blockPos.add(-(i + 1) + random.nextInt(2 + i * 2), 0 - random.nextInt(2), -(i + 1) + random.nextInt(2 + i * 2));
			}

			return true;
		}
	}
}
