package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class PillarBlock extends Block {
	public static final EnumProperty<Direction.Axis> PILLAR_AXIS = Properties.AXIS;

	public PillarBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.getDefaultState().withProperty(PILLAR_AXIS, Direction.Axis.Y));
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch ((Direction.Axis)state.getProperty(PILLAR_AXIS)) {
					case X:
						return state.withProperty(PILLAR_AXIS, Direction.Axis.Z);
					case Z:
						return state.withProperty(PILLAR_AXIS, Direction.Axis.X);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(PILLAR_AXIS);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(PILLAR_AXIS, context.method_16151().getAxis());
	}
}
