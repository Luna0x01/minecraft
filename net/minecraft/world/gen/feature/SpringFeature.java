package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3899;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SpringFeature extends class_3844<class_3899> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3899 arg) {
		if (!Block.method_16585(iWorld.getBlockState(blockPos.up()).getBlock())) {
			return false;
		} else if (!Block.method_16585(iWorld.getBlockState(blockPos.down()).getBlock())) {
			return false;
		} else {
			BlockState blockState = iWorld.getBlockState(blockPos);
			if (!blockState.isAir() && !Block.method_16585(blockState.getBlock())) {
				return false;
			} else {
				int i = 0;
				int j = 0;
				if (Block.method_16585(iWorld.getBlockState(blockPos.west()).getBlock())) {
					j++;
				}

				if (Block.method_16585(iWorld.getBlockState(blockPos.east()).getBlock())) {
					j++;
				}

				if (Block.method_16585(iWorld.getBlockState(blockPos.north()).getBlock())) {
					j++;
				}

				if (Block.method_16585(iWorld.getBlockState(blockPos.south()).getBlock())) {
					j++;
				}

				int k = 0;
				if (iWorld.method_8579(blockPos.west())) {
					k++;
				}

				if (iWorld.method_8579(blockPos.east())) {
					k++;
				}

				if (iWorld.method_8579(blockPos.north())) {
					k++;
				}

				if (iWorld.method_8579(blockPos.south())) {
					k++;
				}

				if (j == 3 && k == 1) {
					iWorld.setBlockState(blockPos, arg.field_19255.getDefaultState().method_17813(), 2);
					iWorld.method_16340().schedule(blockPos, arg.field_19255, 0);
					i++;
				}

				return i > 0;
			}
		}
	}
}
