package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3884 extends class_3844<class_3880> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3880 arg) {
		for (int i = 0; i < arg.field_19236.length; i++) {
			if (random.nextFloat() < arg.field_19238[i]) {
				return this.method_17403(arg.field_19236[i], arg.field_19237[i], iWorld, chunkGenerator, random, blockPos);
			}
		}

		return this.method_17403(arg.field_19239, arg.field_19240, iWorld, chunkGenerator, random, blockPos);
	}

	<FC extends class_3845> boolean method_17403(
		class_3844<FC> arg, class_3845 arg2, IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos
	) {
		return arg.method_17343(iWorld, chunkGenerator, random, blockPos, (FC)arg2);
	}
}
