package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3858 extends class_3844<class_3871> {
	public BlockState method_17378(Random random) {
		return random.nextInt(4) == 0 ? Blocks.FERN.getDefaultState() : Blocks.GRASS.getDefaultState();
	}

	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		BlockState blockState = this.method_17378(random);

		for (BlockState blockState2 = iWorld.getBlockState(blockPos);
			(blockState2.isAir() || blockState2.isIn(BlockTags.LEAVES)) && blockPos.getY() > 0;
			blockState2 = iWorld.getBlockState(blockPos)
		) {
			blockPos = blockPos.down();
		}

		int i = 0;

		for (int j = 0; j < 128; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.method_8579(blockPos2) && blockState.canPlaceAt(iWorld, blockPos2)) {
				iWorld.setBlockState(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}
}
