package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public abstract class class_3823 extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		BlockState blockState = BlockTags.CORAL_BLOCKS.getRandom(random).getDefaultState();
		return this.method_17315(iWorld, random, blockPos, blockState);
	}

	protected abstract boolean method_17315(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState);

	protected boolean method_17316(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos.up();
		BlockState blockState2 = iWorld.getBlockState(blockPos);
		if ((blockState2.getBlock() == Blocks.WATER || blockState2.isIn(BlockTags.CORALS)) && iWorld.getBlockState(blockPos2).getBlock() == Blocks.WATER) {
			iWorld.setBlockState(blockPos, blockState, 3);
			if (random.nextFloat() < 0.25F) {
				iWorld.setBlockState(blockPos2, BlockTags.CORALS.getRandom(random).getDefaultState(), 2);
			} else if (random.nextFloat() < 0.05F) {
				iWorld.setBlockState(blockPos2, Blocks.SEA_PICKLE.getDefaultState().withProperty(class_3719.field_18465, Integer.valueOf(random.nextInt(4) + 1)), 2);
			}

			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (random.nextFloat() < 0.2F) {
					BlockPos blockPos3 = blockPos.offset(direction);
					if (iWorld.getBlockState(blockPos3).getBlock() == Blocks.WATER) {
						BlockState blockState3 = BlockTags.WALL_CORALS.getRandom(random).getDefaultState().withProperty(DeadCoralWallFanBlock.FACING, direction);
						iWorld.setBlockState(blockPos3, blockState3, 2);
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
