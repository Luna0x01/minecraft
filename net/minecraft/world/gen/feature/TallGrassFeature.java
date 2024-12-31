package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3909;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TallGrassFeature extends class_3844<class_3909> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3909 arg) {
		for (BlockState blockState = iWorld.getBlockState(blockPos);
			(blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) && blockPos.getY() > 0;
			blockState = iWorld.getBlockState(blockPos)
		) {
			blockPos = blockPos.down();
		}

		int i = 0;

		for (int j = 0; j < 128; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.method_8579(blockPos2) && arg.field_19265.canPlaceAt(iWorld, blockPos2)) {
				iWorld.setBlockState(blockPos2, arg.field_19265, 2);
				i++;
			}
		}

		return i > 0;
	}
}
