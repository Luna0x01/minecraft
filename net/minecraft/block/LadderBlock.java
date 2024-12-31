package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;

public class LadderBlock extends Block implements FluidDrainable, FluidFillable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty field_18383 = Properties.WATERLOGGED;
	protected static final VoxelShape field_18384 = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	protected static final VoxelShape field_18385 = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18386 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape field_18387 = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);

	protected LadderBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.NORTH).withProperty(field_18383, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		switch ((Direction)state.getProperty(FACING)) {
			case NORTH:
				return field_18387;
			case SOUTH:
				return field_18386;
			case WEST:
				return field_18385;
			case EAST:
			default:
				return field_18384;
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	private boolean method_16690(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState = blockView.getBlockState(blockPos);
		boolean bl = method_14309(blockState.getBlock());
		return !bl && blockState.getRenderLayer(blockView, blockPos, direction) == BlockRenderLayer.SOLID && !blockState.emitsRedstonePower();
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		Direction direction = state.getProperty(FACING);
		return this.method_16690(world, pos.offset(direction.getOpposite()), direction);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (direction.getOpposite() == state.getProperty(FACING) && !state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			if ((Boolean)state.getProperty(field_18383)) {
				world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
			}

			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		if (!context.method_16019()) {
			BlockState blockState = context.getWorld().getBlockState(context.getBlockPos().offset(context.method_16151().getOpposite()));
			if (blockState.getBlock() == this && blockState.getProperty(FACING) == context.method_16151()) {
				return null;
			}
		}

		BlockState blockState2 = this.getDefaultState();
		RenderBlockView renderBlockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());

		for (Direction direction : context.method_16021()) {
			if (direction.getAxis().isHorizontal()) {
				blockState2 = blockState2.withProperty(FACING, direction.getOpposite());
				if (blockState2.canPlaceAt(renderBlockView, blockPos)) {
					return blockState2.withProperty(field_18383, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
				}
			}
		}

		return null;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
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
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, field_18383);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if ((Boolean)state.getProperty(field_18383)) {
			world.setBlockState(pos, state.withProperty(field_18383, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getProperty(field_18383) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return !(Boolean)state.getProperty(field_18383) && fluid == Fluids.WATER;
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!(Boolean)state.getProperty(field_18383) && fluidState.getFluid() == Fluids.WATER) {
			if (!world.method_16390()) {
				world.setBlockState(pos, state.withProperty(field_18383, Boolean.valueOf(true)), 3);
				world.method_16340().schedule(pos, fluidState.getFluid(), fluidState.getFluid().method_17778(world));
			}

			return true;
		} else {
			return false;
		}
	}
}
