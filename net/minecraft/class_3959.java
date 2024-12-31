package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

public class class_3959 extends class_3945<class_3939> {
	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3939 arg, class_3844<C> arg2, C arg3
	) {
		double d = Biome.FOLIAGE_NOISE.noise((double)blockPos.getX() / arg.field_19325, (double)blockPos.getZ() / arg.field_19325);
		int i = (int)Math.ceil(d * (double)arg.field_19324);

		for (int j = 0; j < i; j++) {
			int k = random.nextInt(16);
			int l = random.nextInt(16);
			int m = iWorld.method_16372(class_3804.class_3805.OCEAN_FLOOR_WG, blockPos.getX() + k, blockPos.getZ() + l);
			arg2.method_17343(iWorld, chunkGenerator, random, new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l), arg3);
		}

		return false;
	}
}
