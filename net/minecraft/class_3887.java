package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3887 extends class_3844<class_3828> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockPos blockPos, class_3828 arg) {
		int i = 0;

		for (int j = 0; j < arg.field_19078; j++) {
			int k = random.nextInt(8) - random.nextInt(8);
			int l = random.nextInt(8) - random.nextInt(8);
			int m = iWorld.method_16372(class_3804.class_3805.OCEAN_FLOOR, blockPos.getX() + k, blockPos.getZ() + l);
			BlockPos blockPos2 = new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l);
			BlockState blockState = Blocks.SEA_PICKLE.getDefaultState().withProperty(class_3719.field_18465, Integer.valueOf(random.nextInt(4) + 1));
			if (iWorld.getBlockState(blockPos2).getBlock() == Blocks.WATER && blockState.canPlaceAt(iWorld, blockPos2)) {
				iWorld.setBlockState(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}
}
