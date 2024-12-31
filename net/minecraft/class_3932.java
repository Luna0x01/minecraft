package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3932 extends class_3945<class_3937> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3937 arg, class_3844<C> arg2, C arg3
	) {
		int i = arg.field_19319;
		if (random.nextFloat() < arg.field_19320) {
			i += arg.field_19321;
		}

		for (int j = 0; j < i; j++) {
			int k = random.nextInt(16);
			int l = random.nextInt(16);
			arg2.method_17343(iWorld, chunkGenerator, random, iWorld.method_16373(class_3804.class_3805.MOTION_BLOCKING, blockPos.add(k, 0, l)), arg3);
		}

		return true;
	}
}
