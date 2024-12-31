package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3925 extends class_3945<class_3941> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3941 arg, class_3844<C> arg2, C arg3
	) {
		int i = arg.field_19328;
		int j = arg.field_19329;
		int k = arg.field_19330;

		for (int l = 0; l < i; l++) {
			int m = random.nextInt(16);
			int n = random.nextInt(k) + random.nextInt(k) - k + j;
			int o = random.nextInt(16);
			BlockPos blockPos2 = blockPos.add(m, n, o);
			arg2.method_17343(iWorld, chunkGenerator, random, blockPos2, arg3);
		}

		return true;
	}
}
