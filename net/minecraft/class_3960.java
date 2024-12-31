package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3960 extends class_3945<class_3940> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3940 arg, class_3844<C> arg2, C arg3
	) {
		int i = random.nextInt(arg.field_19327 - arg.field_19326) + arg.field_19326;

		for (int j = 0; j < i; j++) {
			int k = random.nextInt(16);
			int l = random.nextInt(16);
			int m = iWorld.method_16372(class_3804.class_3805.OCEAN_FLOOR_WG, blockPos.getX() + k, blockPos.getZ() + l);
			arg2.method_17343(iWorld, chunkGenerator, random, new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l), arg3);
		}

		return false;
	}
}
