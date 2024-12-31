package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3952 extends class_3945<class_3951> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3951 arg, class_3844<C> arg2, C arg3
	) {
		int i = arg.field_19332;

		for (int j = 0; j < i; j++) {
			int k = random.nextInt(16);
			int l = random.nextInt(chunkGenerator.method_17026());
			int m = random.nextInt(16);
			arg2.method_17343(iWorld, chunkGenerator, random, blockPos.add(k, l, m), arg3);
		}

		return true;
	}
}
