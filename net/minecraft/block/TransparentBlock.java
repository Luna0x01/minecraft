package net.minecraft.block;

import net.minecraft.util.math.Direction;

public class TransparentBlock extends Block {
	protected TransparentBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public boolean method_16573(BlockState blockState, BlockState blockState2, Direction direction) {
		return blockState2.getBlock() == this ? true : super.method_16573(blockState, blockState2, direction);
	}
}
