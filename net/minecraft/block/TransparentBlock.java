package net.minecraft.block;

import net.minecraft.util.math.Direction;

public class TransparentBlock extends Block {
	protected TransparentBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isSideInvisible(BlockState blockState, BlockState blockState2, Direction direction) {
		return blockState2.getBlock() == this ? true : super.isSideInvisible(blockState, blockState2, direction);
	}
}
