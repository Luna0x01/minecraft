package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public abstract class class_3848 extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		BlockState blockState = this.method_17350(random, blockPos);
		int i = 0;

		for (int j = 0; j < 64; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.method_8579(blockPos2) && blockPos2.getY() < 255 && blockState.canPlaceAt(iWorld, blockPos2)) {
				iWorld.setBlockState(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}

	public abstract BlockState method_17350(Random random, BlockPos blockPos);
}
