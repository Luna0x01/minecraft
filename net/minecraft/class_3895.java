package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3895 extends class_3844<class_3894> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3894 arg) {
		if (arg.field_19250.contains(iWorld.getBlockState(blockPos.down()))
			&& arg.field_19251.contains(iWorld.getBlockState(blockPos))
			&& arg.field_19252.contains(iWorld.getBlockState(blockPos.up()))) {
			iWorld.setBlockState(blockPos, arg.field_19249, 2);
			return true;
		} else {
			return false;
		}
	}
}
