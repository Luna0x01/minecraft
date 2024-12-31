package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3950 extends class_3945<class_3948> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3948 arg, class_3844<C> arg2, C arg3
	) {
		if (random.nextInt(arg.field_19331) == 0) {
			int i = random.nextInt(16);
			int j = random.nextInt(chunkGenerator.method_17026());
			int k = random.nextInt(16);
			arg2.method_17343(iWorld, chunkGenerator, random, blockPos.add(i, j, k), arg3);
		}

		return true;
	}
}
