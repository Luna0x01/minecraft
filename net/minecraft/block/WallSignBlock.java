package net.minecraft.block;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WallSignBlock extends AbstractSignBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	protected static final Box field_12832 = new Box(0.0, 0.28125, 0.0, 0.125, 0.78125, 1.0);
	protected static final Box field_12833 = new Box(0.875, 0.28125, 0.0, 1.0, 0.78125, 1.0);
	protected static final Box field_12834 = new Box(0.0, 0.28125, 0.0, 1.0, 0.78125, 0.125);
	protected static final Box field_12835 = new Box(0.0, 0.28125, 0.875, 1.0, 0.78125, 1.0);

	public WallSignBlock() {
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		switch ((Direction)state.get(FACING)) {
			case NORTH:
			default:
				return field_12835;
			case SOUTH:
				return field_12834;
			case WEST:
				return field_12833;
			case EAST:
				return field_12832;
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		Direction direction = blockState.get(FACING);
		if (!world.getBlockState(blockPos.offset(direction.getOpposite())).getMaterial().isSolid()) {
			this.dropAsItem(world, blockPos, blockState, 0);
			world.setAir(blockPos);
		}

		super.method_8641(blockState, world, blockPos, block);
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction direction = Direction.getById(data);
		if (direction.getAxis() == Direction.Axis.Y) {
			direction = Direction.NORTH;
		}

		return this.getDefaultState().with(FACING, direction);
	}

	@Override
	public int getData(BlockState state) {
		return ((Direction)state.get(FACING)).getId();
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING);
	}
}
