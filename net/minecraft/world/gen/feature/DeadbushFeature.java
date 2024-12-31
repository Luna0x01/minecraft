package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3871;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadBushBlock;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class DeadbushFeature extends class_3844<class_3871> {
	private static final DeadBushBlock field_19079 = (DeadBushBlock)Blocks.DEAD_BUSH;

	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		for (BlockState blockState = iWorld.getBlockState(blockPos);
			(blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) && blockPos.getY() > 0;
			blockState = iWorld.getBlockState(blockPos)
		) {
			blockPos = blockPos.down();
		}

		BlockState blockState2 = field_19079.getDefaultState();

		for (int i = 0; i < 4; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.method_8579(blockPos2) && blockState2.canPlaceAt(iWorld, blockPos2)) {
				iWorld.setBlockState(blockPos2, blockState2, 2);
			}
		}

		return true;
	}
}
