package net.minecraft.block;

import net.minecraft.state.property.DirectionProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

public abstract class HorizontalFacingBlock extends Block {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	protected HorizontalFacingBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getProperty(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(FACING)));
	}
}
