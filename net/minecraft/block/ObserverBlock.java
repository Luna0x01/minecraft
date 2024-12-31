package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ObserverBlock extends FacingBlock {
	public static final BooleanProperty field_18418 = Properties.POWERED;

	public ObserverBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.SOUTH).withProperty(field_18418, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, field_18418);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getProperty(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(FACING)));
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.getProperty(field_18418)) {
			world.setBlockState(pos, state.withProperty(field_18418, Boolean.valueOf(false)), 2);
		} else {
			world.setBlockState(pos, state.withProperty(field_18418, Boolean.valueOf(true)), 2);
			world.getBlockTickScheduler().schedule(pos, this, 2);
		}

		this.method_13713(world, pos, state);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (state.getProperty(FACING) == direction && !(Boolean)state.getProperty(field_18418)) {
			this.method_16710(world, pos);
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	private void method_16710(IWorld iWorld, BlockPos blockPos) {
		if (!iWorld.method_16390() && !iWorld.getBlockTickScheduler().method_16417(blockPos, this)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 2);
		}
	}

	protected void method_13713(World world, BlockPos pos, BlockState state) {
		Direction direction = state.getProperty(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		world.updateNeighbor(blockPos, this, pos);
		world.updateNeighborsExcept(blockPos, this, direction);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getProperty(field_18418) && state.getProperty(FACING) == direction ? 15 : 0;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (state.getBlock() != oldState.getBlock()) {
			if (!world.method_16390() && (Boolean)state.getProperty(field_18418) && !world.getBlockTickScheduler().method_16417(pos, this)) {
				BlockState blockState = state.withProperty(field_18418, Boolean.valueOf(false));
				world.setBlockState(pos, blockState, 18);
				this.method_13713(world, pos, blockState);
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			if (!world.isClient && (Boolean)state.getProperty(field_18418) && world.getBlockTickScheduler().method_16417(pos, this)) {
				this.method_13713(world, pos, state.withProperty(field_18418, Boolean.valueOf(false)));
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(FACING, context.method_16020().getOpposite().getOpposite());
	}
}
