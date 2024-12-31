package net.minecraft.block;

import net.minecraft.class_3716;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailBlock extends AbstractRailBlock {
	public static final EnumProperty<RailShape> field_18435 = Properties.RAIL_SHAPE;

	protected RailBlock(Block.Builder builder) {
		super(false, builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18435, RailShape.NORTH_SOUTH));
	}

	@Override
	protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
		if (neighbor.getDefaultState().emitsRedstonePower() && new class_3716(world, pos, state).method_16719() == 3) {
			this.updateBlockState(world, pos, state, false);
		}
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return field_18435;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				switch ((RailShape)state.getProperty(field_18435)) {
					case ASCENDING_EAST:
						return state.withProperty(field_18435, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.withProperty(field_18435, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
						return state.withProperty(field_18435, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18435, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.withProperty(field_18435, RailShape.NORTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(field_18435, RailShape.NORTH_EAST);
					case NORTH_WEST:
						return state.withProperty(field_18435, RailShape.SOUTH_EAST);
					case NORTH_EAST:
						return state.withProperty(field_18435, RailShape.SOUTH_WEST);
				}
			case COUNTERCLOCKWISE_90:
				switch ((RailShape)state.getProperty(field_18435)) {
					case ASCENDING_EAST:
						return state.withProperty(field_18435, RailShape.ASCENDING_NORTH);
					case ASCENDING_WEST:
						return state.withProperty(field_18435, RailShape.ASCENDING_SOUTH);
					case ASCENDING_NORTH:
						return state.withProperty(field_18435, RailShape.ASCENDING_WEST);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18435, RailShape.ASCENDING_EAST);
					case SOUTH_EAST:
						return state.withProperty(field_18435, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return state.withProperty(field_18435, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return state.withProperty(field_18435, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return state.withProperty(field_18435, RailShape.NORTH_WEST);
					case NORTH_SOUTH:
						return state.withProperty(field_18435, RailShape.EAST_WEST);
					case EAST_WEST:
						return state.withProperty(field_18435, RailShape.NORTH_SOUTH);
				}
			case CLOCKWISE_90:
				switch ((RailShape)state.getProperty(field_18435)) {
					case ASCENDING_EAST:
						return state.withProperty(field_18435, RailShape.ASCENDING_SOUTH);
					case ASCENDING_WEST:
						return state.withProperty(field_18435, RailShape.ASCENDING_NORTH);
					case ASCENDING_NORTH:
						return state.withProperty(field_18435, RailShape.ASCENDING_EAST);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18435, RailShape.ASCENDING_WEST);
					case SOUTH_EAST:
						return state.withProperty(field_18435, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(field_18435, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return state.withProperty(field_18435, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return state.withProperty(field_18435, RailShape.SOUTH_EAST);
					case NORTH_SOUTH:
						return state.withProperty(field_18435, RailShape.EAST_WEST);
					case EAST_WEST:
						return state.withProperty(field_18435, RailShape.NORTH_SOUTH);
				}
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		RailShape railShape = state.getProperty(field_18435);
		switch (mirror) {
			case LEFT_RIGHT:
				switch (railShape) {
					case ASCENDING_NORTH:
						return state.withProperty(field_18435, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18435, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.withProperty(field_18435, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return state.withProperty(field_18435, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return state.withProperty(field_18435, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return state.withProperty(field_18435, RailShape.SOUTH_EAST);
					default:
						return super.withMirror(state, mirror);
				}
			case FRONT_BACK:
				switch (railShape) {
					case ASCENDING_EAST:
						return state.withProperty(field_18435, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.withProperty(field_18435, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
					case ASCENDING_SOUTH:
					default:
						break;
					case SOUTH_EAST:
						return state.withProperty(field_18435, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(field_18435, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return state.withProperty(field_18435, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return state.withProperty(field_18435, RailShape.NORTH_WEST);
				}
		}

		return super.withMirror(state, mirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18435);
	}
}
