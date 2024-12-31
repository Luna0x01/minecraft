package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3968 extends class_3945<class_3831> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3831 arg, class_3844<C> arg2, C arg3
	) {
		int i = random.nextInt(Math.max(arg.field_19085, 1));

		for (int j = 0; j < i; j++) {
			int k = random.nextInt(16);
			int l = random.nextInt(arg.field_19088 - arg.field_19087) + arg.field_19086;
			int m = random.nextInt(16);
			arg2.method_17343(iWorld, chunkGenerator, random, blockPos.add(k, l, m), arg3);
		}

		return true;
	}
}
