package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;

public class CoralParentBlock extends Block implements FluidDrainable, FluidFillable {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private static final VoxelShape PARENT_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

	protected CoralParentBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(WATERLOGGED, Boolean.valueOf(true)));
	}

	protected void checkLivingConditions(BlockState state, IWorld world, BlockPos pos) {
		if (!isInWater(state, world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 60 + world.getRandom().nextInt(40));
		}
	}

	protected static boolean isInWater(BlockState state, BlockView world, BlockPos pos) {
		if ((Boolean)state.getProperty(WATERLOGGED)) {
			return true;
		} else {
			for (Direction direction : Direction.values()) {
				if (world.getFluidState(pos.offset(direction)).matches(FluidTags.WATER)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		return this.getDefaultState().withProperty(WATERLOGGED, Boolean.valueOf(fluidState.matches(FluidTags.WATER) && fluidState.method_17811() == 8));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return PARENT_SHAPE;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.getProperty(WATERLOGGED)) {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
		}

		return direction == Direction.DOWN && !this.canPlaceAt(state, world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		return world.getBlockState(pos.down()).method_16913();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getProperty(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if ((Boolean)state.getProperty(WATERLOGGED)) {
			world.setBlockState(pos, state.withProperty(WATERLOGGED, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return !(Boolean)state.getProperty(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!(Boolean)state.getProperty(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
			if (!world.method_16390()) {
				world.setBlockState(pos, state.withProperty(WATERLOGGED, Boolean.valueOf(true)), 3);
				world.method_16340().schedule(pos, fluidState.getFluid(), fluidState.getFluid().method_17778(world));
			}

			return true;
		} else {
			return false;
		}
	}
}
