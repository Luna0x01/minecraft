package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3956 extends class_3945<class_3870> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3870 arg, class_3844<C> arg2, C arg3
	) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int k = i * 4 + 1 + random.nextInt(3);
				int l = j * 4 + 1 + random.nextInt(3);
				arg2.method_17343(iWorld, chunkGenerator, random, iWorld.method_16373(class_3804.class_3805.MOTION_BLOCKING, blockPos.add(k, 0, l)), arg3);
			}
		}

		return true;
	}
}
