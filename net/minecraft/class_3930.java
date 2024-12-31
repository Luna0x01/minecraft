package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3930 extends class_3945<class_3935> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3935 arg, class_3844<C> arg2, C arg3
	) {
		for (int i = 0; i < arg.field_19316; i++) {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			arg2.method_17343(iWorld, chunkGenerator, random, new BlockPos(j, iWorld.method_16372(class_3804.class_3805.OCEAN_FLOOR_WG, j, k), k), arg3);
		}

		return true;
	}
}
