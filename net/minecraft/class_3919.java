package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3919 extends class_3945<class_3934> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3934 arg, class_3844<C> arg2, C arg3
	) {
		if (random.nextFloat() < 1.0F / (float)arg.field_19315) {
			int i = random.nextInt(16);
			int j = random.nextInt(16);
			BlockPos blockPos2 = iWorld.method_16373(class_3804.class_3805.MOTION_BLOCKING, blockPos.add(i, 0, j));
			arg2.method_17343(iWorld, chunkGenerator, random, blockPos2, arg3);
		}

		return true;
	}
}
