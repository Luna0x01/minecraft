package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3871;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class LilyPadFeature extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		BlockPos blockPos2 = blockPos;

		while (blockPos2.getY() > 0) {
			BlockPos blockPos3 = blockPos2.down();
			if (!iWorld.method_8579(blockPos3)) {
				break;
			}

			blockPos2 = blockPos3;
		}

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos4 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			BlockState blockState = Blocks.LILY_PAD.getDefaultState();
			if (iWorld.method_8579(blockPos4) && blockState.canPlaceAt(iWorld, blockPos4)) {
				iWorld.setBlockState(blockPos4, blockState, 2);
			}
		}

		return true;
	}
}
