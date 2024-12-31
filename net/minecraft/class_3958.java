package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3958 extends class_3945<class_3870> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3870 arg, class_3844<C> arg2, C arg3
	) {
		int i = random.nextInt(16);
		int j = random.nextInt(16);
		int k = iWorld.method_16372(class_3804.class_3805.OCEAN_FLOOR_WG, blockPos.getX() + i, blockPos.getZ() + j);
		arg2.method_17343(iWorld, chunkGenerator, random, new BlockPos(blockPos.getX() + i, k, blockPos.getZ() + j), arg3);
		return false;
	}
}
