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

public class PumpkinFeature extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		int i = 0;
		BlockState blockState = Blocks.PUMPKIN.getDefaultState();

		for (int j = 0; j < 64; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.method_8579(blockPos2) && iWorld.getBlockState(blockPos2.down()).getBlock() == Blocks.GRASS_BLOCK) {
				iWorld.setBlockState(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}
}
