package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3881 extends class_3844<class_3882> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3882 arg) {
		int i = random.nextInt(5) - 3 + arg.field_19243;

		for (int j = 0; j < i; j++) {
			int k = random.nextInt(arg.field_19241.length);
			this.method_17398(arg.field_19241[k], arg.field_19242[k], iWorld, chunkGenerator, random, blockPos);
		}

		return true;
	}

	<FC extends class_3845> boolean method_17398(
		class_3844<FC> arg, class_3845 arg2, IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos
	) {
		return arg.method_17343(iWorld, chunkGenerator, random, blockPos, (FC)arg2);
	}
}
