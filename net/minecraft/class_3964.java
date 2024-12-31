package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3964 extends class_3945<class_3935> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3935 arg, class_3844<C> arg2, C arg3
	) {
		for (int i = 0; i < random.nextInt(random.nextInt(arg.field_19316) + 1); i++) {
			int j = random.nextInt(16);
			int k = random.nextInt(120) + 4;
			int l = random.nextInt(16);
			arg2.method_17343(iWorld, chunkGenerator, random, blockPos.add(j, k, l), arg3);
		}

		return true;
	}
}
