package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractSignBlock extends BlockWithEntity implements FluidDrainable, FluidFillable {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

	protected AbstractSignBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.getProperty(WATERLOGGED)) {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean method_13704(BlockState state) {
		return true;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new SignBlockEntity();
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			return blockEntity instanceof SignBlockEntity && ((SignBlockEntity)blockEntity).onActivate(player);
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
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
	public FluidState getFluidState(BlockState state) {
		return state.getProperty(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
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
