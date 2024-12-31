package net.minecraft;

import java.util.Random;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3879 extends class_3844<class_3878> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3878 arg) {
		boolean bl = random.nextBoolean();
		return bl
			? this.method_17396(arg.field_19232, arg.field_19233, iWorld, chunkGenerator, random, blockPos)
			: this.method_17396(arg.field_19234, arg.field_19235, iWorld, chunkGenerator, random, blockPos);
	}

	<FC extends class_3845> boolean method_17396(
		class_3844<FC> arg, class_3845 arg2, IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos
	) {
		return arg.method_17343(iWorld, chunkGenerator, random, blockPos, (FC)arg2);
	}
}
