package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3888 extends class_3844<class_3889> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3889 arg) {
		int i = 0;

		for (int j = 0; j < arg.field_19246; j++) {
			int k = random.nextInt(8) - random.nextInt(8);
			int l = random.nextInt(8) - random.nextInt(8);
			int m = iWorld.method_16372(class_3804.class_3805.OCEAN_FLOOR, blockPos.getX() + k, blockPos.getZ() + l);
			BlockPos blockPos2 = new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l);
			if (iWorld.getBlockState(blockPos2).getBlock() == Blocks.WATER) {
				boolean bl = random.nextDouble() < arg.field_19247;
				BlockState blockState = bl ? Blocks.TALL_SEAGRASS.getDefaultState() : Blocks.SEAGRASS.getDefaultState();
				if (blockState.canPlaceAt(iWorld, blockPos2)) {
					if (bl) {
						BlockState blockState2 = blockState.withProperty(class_3730.field_18527, DoubleBlockHalf.UPPER);
						BlockPos blockPos3 = blockPos2.up();
						if (iWorld.getBlockState(blockPos3).getBlock() == Blocks.WATER) {
							iWorld.setBlockState(blockPos2, blockState, 2);
							iWorld.setBlockState(blockPos3, blockState2, 2);
						}
					} else {
						iWorld.setBlockState(blockPos2, blockState, 2);
					}

					i++;
				}
			}
		}

		return i > 0;
	}
}
