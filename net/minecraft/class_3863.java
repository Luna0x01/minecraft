package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3863 extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		int i = 0;
		int j = iWorld.method_16372(class_3804.class_3805.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
		BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
		if (iWorld.getBlockState(blockPos2).getBlock() == Blocks.WATER) {
			BlockState blockState = Blocks.KELP.getDefaultState();
			BlockState blockState2 = Blocks.KELP_PLANT.getDefaultState();
			int k = 1 + random.nextInt(10);

			for (int l = 0; l <= k; l++) {
				if (iWorld.getBlockState(blockPos2).getBlock() == Blocks.WATER
					&& iWorld.getBlockState(blockPos2.up()).getBlock() == Blocks.WATER
					&& blockState2.canPlaceAt(iWorld, blockPos2)) {
					if (l == k) {
						iWorld.setBlockState(blockPos2, blockState.withProperty(class_3708.field_18380, Integer.valueOf(random.nextInt(23))), 2);
						i++;
					} else {
						iWorld.setBlockState(blockPos2, blockState2, 2);
					}
				} else if (l > 0) {
					BlockPos blockPos3 = blockPos2.down();
					if (blockState.canPlaceAt(iWorld, blockPos3) && iWorld.getBlockState(blockPos3.down()).getBlock() != Blocks.KELP) {
						iWorld.setBlockState(blockPos3, blockState.withProperty(class_3708.field_18380, Integer.valueOf(random.nextInt(23))), 2);
						i++;
					}
					break;
				}

				blockPos2 = blockPos2.up();
			}
		}

		return i > 0;
	}
}
