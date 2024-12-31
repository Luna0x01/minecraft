package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3897 extends class_3844<class_3896> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3896 arg) {
		int i = random.nextInt(arg.field_19253.length);
		return this.method_17417(arg.field_19253[i], arg.field_19254[i], iWorld, chunkGenerator, random, blockPos);
	}

	<FC extends class_3845> boolean method_17417(
		class_3844<FC> arg, class_3845 arg2, IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos
	) {
		return arg.method_17343(iWorld, chunkGenerator, random, blockPos, (FC)arg2);
	}
}
