package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3967 extends class_3945<class_3935> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3935 arg, class_3844<C> arg2, C arg3
	) {
		int i = iWorld.method_8483() / 2 + 1;

		for (int j = 0; j < arg.field_19316; j++) {
			int k = random.nextInt(16);
			int l = i - 5 + random.nextInt(10);
			int m = random.nextInt(16);
			arg2.method_17343(iWorld, chunkGenerator, random, blockPos.add(k, l, m), arg3);
		}

		return true;
	}
}
