package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3927 extends class_3945<class_3935> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3935 arg, class_3844<C> arg2, C arg3
	) {
		for (int i = 0; i < arg.field_19316; i++) {
			int j = random.nextInt(16);
			int k = 64;
			int l = random.nextInt(16);
			arg2.method_17343(iWorld, chunkGenerator, random, blockPos.add(j, 64, l), arg3);
		}

		return true;
	}
}
