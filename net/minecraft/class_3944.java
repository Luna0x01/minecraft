package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3944 extends class_3945<class_3870> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3870 arg, class_3844<C> arg2, C arg3
	) {
		boolean bl = false;
		if (random.nextInt(14) == 0) {
			bl |= arg2.method_17343(iWorld, chunkGenerator, random, blockPos.add(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16)), arg3);
			if (random.nextInt(4) == 0) {
				bl |= arg2.method_17343(iWorld, chunkGenerator, random, blockPos.add(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16)), arg3);
			}
		}

		return bl;
	}
}
