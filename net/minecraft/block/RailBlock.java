package net.minecraft.block;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailBlock extends AbstractRailBlock {
	public static final EnumProperty<AbstractRailBlock.RailShapeType> SHAPE = EnumProperty.of("shape", AbstractRailBlock.RailShapeType.class);

	protected RailBlock() {
		super(false);
		this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_SOUTH));
	}

	@Override
	protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
		if (neighbor.getDefaultState().emitsRedstonePower() && new AbstractRailBlock.RailPlacementHelper(world, pos, state).getVerticalNearbyRailCount() == 3) {
			this.updateBlockState(world, pos, state, false);
		}
	}

	@Override
	public Property<AbstractRailBlock.RailShapeType> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(SHAPE, AbstractRailBlock.RailShapeType.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((AbstractRailBlock.RailShapeType)state.get(SHAPE)).getData();
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				switch ((AbstractRailBlock.RailShapeType)state.get(SHAPE)) {
					case ASCENDING_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_EAST);
					case ASCENDING_NORTH:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_WEST);
					case SOUTH_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_EAST);
					case NORTH_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.SOUTH_EAST);
					case NORTH_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.SOUTH_WEST);
				}
			case COUNTERCLOCKWISE_90:
				switch ((AbstractRailBlock.RailShapeType)state.get(SHAPE)) {
					case ASCENDING_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_NORTH);
					case ASCENDING_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_SOUTH);
					case ASCENDING_NORTH:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_WEST);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_EAST);
					case SOUTH_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_EAST);
					case SOUTH_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.SOUTH_EAST);
					case NORTH_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.SOUTH_WEST);
					case NORTH_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_WEST);
					case NORTH_SOUTH:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.EAST_WEST);
					case EAST_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_SOUTH);
				}
			case CLOCKWISE_90:
				switch ((AbstractRailBlock.RailShapeType)state.get(SHAPE)) {
					case ASCENDING_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_SOUTH);
					case ASCENDING_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_NORTH);
					case ASCENDING_NORTH:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_EAST);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_WEST);
					case SOUTH_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.SOUTH_WEST);
					case SOUTH_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_WEST);
					case NORTH_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_EAST);
					case NORTH_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.SOUTH_EAST);
					case NORTH_SOUTH:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.EAST_WEST);
					case EAST_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_SOUTH);
				}
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		AbstractRailBlock.RailShapeType railShapeType = state.get(SHAPE);
		switch (mirror) {
			case LEFT_RIGHT:
				switch (railShapeType) {
					case ASCENDING_NORTH:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_EAST);
					case SOUTH_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_WEST);
					case NORTH_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.SOUTH_WEST);
					case NORTH_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.SOUTH_EAST);
					default:
						return super.withMirror(state, mirror);
				}
			case FRONT_BACK:
				switch (railShapeType) {
					case ASCENDING_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.ASCENDING_EAST);
					case ASCENDING_NORTH:
					case ASCENDING_SOUTH:
					default:
						break;
					case SOUTH_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.SOUTH_WEST);
					case SOUTH_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.SOUTH_EAST);
					case NORTH_WEST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_EAST);
					case NORTH_EAST:
						return state.with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_WEST);
				}
		}

		return super.withMirror(state, mirror);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, SHAPE);
	}
}
