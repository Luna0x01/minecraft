package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

public class class_3954 extends class_3945<class_3832> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3832 arg, class_3844<C> arg2, C arg3
	) {
		double d = Biome.FOLIAGE_NOISE.noise((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0);
		int i = d < arg.field_19089 ? arg.field_19090 : arg.field_19091;

		for (int j = 0; j < i; j++) {
			int k = random.nextInt(16);
			int l = random.nextInt(16);
			int m = iWorld.method_16373(class_3804.class_3805.MOTION_BLOCKING, blockPos.add(k, 0, l)).getY() * 2;
			if (m > 0) {
				int n = random.nextInt(m);
				arg2.method_17343(iWorld, chunkGenerator, random, blockPos.add(k, n, l), arg3);
			}
		}

		return true;
	}
}
