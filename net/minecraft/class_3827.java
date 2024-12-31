package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3827 extends class_3945<class_3936> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3936 arg, class_3844<C> arg2, C arg3
	) {
		for (int i = 0; i < arg.field_19317; i++) {
			if (random.nextFloat() < arg.field_19318) {
				int j = random.nextInt(16);
				int k = random.nextInt(16);
				int l = iWorld.method_16373(class_3804.class_3805.MOTION_BLOCKING, blockPos.add(j, 0, k)).getY() * 2;
				if (l > 0) {
					int m = random.nextInt(l);
					arg2.method_17343(iWorld, chunkGenerator, random, blockPos.add(j, m, k), arg3);
				}
			}
		}

		return true;
	}
}
