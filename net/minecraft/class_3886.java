package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3886 extends class_3844<class_3885> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3885 arg) {
		if (arg.field_19244.test(iWorld.getBlockState(blockPos))) {
			iWorld.setBlockState(blockPos, arg.field_19245, 2);
		}

		return true;
	}
}
