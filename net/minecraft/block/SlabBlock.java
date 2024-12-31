package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class SlabBlock extends Block implements FluidDrainable, FluidFillable {
	public static final EnumProperty<SlabType> field_18486 = Properties.SLAB_TYPE;
	public static final BooleanProperty field_18487 = Properties.WATERLOGGED;
	protected static final VoxelShape field_18488 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape field_18489 = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

	public SlabBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.getDefaultState().withProperty(field_18486, SlabType.BOTTOM).withProperty(field_18487, Boolean.valueOf(false)));
	}

	@Override
	public int getLightSubtracted(BlockState state, BlockView world, BlockPos pos) {
		return world.getMaxLightLevel();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18486, field_18487);
	}

	@Override
	protected boolean requiresSilkTouch() {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		SlabType slabType = state.getProperty(field_18486);
		switch (slabType) {
			case DOUBLE:
				return VoxelShapes.matchesAnywhere();
			case TOP:
				return field_18489;
			default:
				return field_18488;
		}
	}

	@Override
	public boolean method_11568(BlockState state) {
		return state.getProperty(field_18486) == SlabType.DOUBLE || state.getProperty(field_18486) == SlabType.TOP;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		SlabType slabType = state.getProperty(field_18486);
		if (slabType == SlabType.DOUBLE) {
			return BlockRenderLayer.SOLID;
		} else if (direction == Direction.UP && slabType == SlabType.TOP) {
			return BlockRenderLayer.SOLID;
		} else {
			return direction == Direction.DOWN && slabType == SlabType.BOTTOM ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		if (blockState.getBlock() == this) {
			return blockState.withProperty(field_18486, SlabType.DOUBLE).withProperty(field_18487, Boolean.valueOf(false));
		} else {
			FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
			BlockState blockState2 = this.getDefaultState()
				.withProperty(field_18486, SlabType.BOTTOM)
				.withProperty(field_18487, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
			Direction direction = context.method_16151();
			return direction != Direction.DOWN && (direction == Direction.UP || !((double)context.method_16153() > 0.5))
				? blockState2
				: blockState2.withProperty(field_18486, SlabType.TOP);
		}
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return state.getProperty(field_18486) == SlabType.DOUBLE ? 2 : 1;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return state.getProperty(field_18486) == SlabType.DOUBLE;
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext itemPlacementContext) {
		ItemStack itemStack = itemPlacementContext.getItemStack();
		SlabType slabType = state.getProperty(field_18486);
		if (slabType == SlabType.DOUBLE || itemStack.getItem() != this.getItem()) {
			return false;
		} else if (itemPlacementContext.method_16019()) {
			boolean bl = (double)itemPlacementContext.method_16153() > 0.5;
			Direction direction = itemPlacementContext.method_16151();
			return slabType == SlabType.BOTTOM
				? direction == Direction.UP || bl && direction.getAxis().isHorizontal()
				: direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
		} else {
			return true;
		}
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if ((Boolean)state.getProperty(field_18487)) {
			world.setBlockState(pos, state.withProperty(field_18487, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getProperty(field_18487) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return state.getProperty(field_18486) != SlabType.DOUBLE && !(Boolean)state.getProperty(field_18487) && fluid == Fluids.WATER;
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (state.getProperty(field_18486) != SlabType.DOUBLE && !(Boolean)state.getProperty(field_18487) && fluidState.getFluid() == Fluids.WATER) {
			if (!world.method_16390()) {
				world.setBlockState(pos, state.withProperty(field_18487, Boolean.valueOf(true)), 3);
				world.method_16340().schedule(pos, fluidState.getFluid(), fluidState.getFluid().method_17778(world));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.getProperty(field_18487)) {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		switch (environment) {
			case LAND:
				return state.getProperty(field_18486) == SlabType.BOTTOM;
			case WATER:
				return world.getFluidState(pos).matches(FluidTags.WATER);
			case AIR:
				return false;
			default:
				return false;
		}
	}
}
