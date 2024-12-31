package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TrapdoorBlock extends HorizontalFacingBlock implements FluidDrainable, FluidFillable {
	public static final BooleanProperty field_18531 = Properties.OPEN;
	public static final EnumProperty<BlockHalf> field_18532 = Properties.BLOCK_HALF;
	public static final BooleanProperty field_18533 = Properties.POWERED;
	public static final BooleanProperty field_18534 = Properties.WATERLOGGED;
	protected static final VoxelShape field_18535 = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	protected static final VoxelShape field_18536 = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18537 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape field_18538 = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18539 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
	protected static final VoxelShape field_18540 = Block.createCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);

	protected TrapdoorBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(FACING, Direction.NORTH)
				.withProperty(field_18531, Boolean.valueOf(false))
				.withProperty(field_18532, BlockHalf.BOTTOM)
				.withProperty(field_18533, Boolean.valueOf(false))
				.withProperty(field_18534, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		if (!(Boolean)state.getProperty(field_18531)) {
			return state.getProperty(field_18532) == BlockHalf.TOP ? field_18540 : field_18539;
		} else {
			switch ((Direction)state.getProperty(FACING)) {
				case NORTH:
				default:
					return field_18538;
				case SOUTH:
					return field_18537;
				case WEST:
					return field_18536;
				case EAST:
					return field_18535;
			}
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		switch (environment) {
			case LAND:
				return (Boolean)state.getProperty(field_18531);
			case WATER:
				return (Boolean)state.getProperty(field_18534);
			case AIR:
				return (Boolean)state.getProperty(field_18531);
			default:
				return false;
		}
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (this.material == Material.IRON) {
			return false;
		} else {
			state = state.method_16930(field_18531);
			world.setBlockState(pos, state, 2);
			if ((Boolean)state.getProperty(field_18534)) {
				world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
			}

			this.method_11640(player, world, pos, (Boolean)state.getProperty(field_18531));
			return true;
		}
	}

	protected void method_11640(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, boolean bl) {
		if (bl) {
			int i = this.material == Material.IRON ? 1037 : 1007;
			world.syncWorldEvent(playerEntity, i, blockPos, 0);
		} else {
			int j = this.material == Material.IRON ? 1036 : 1013;
			world.syncWorldEvent(playerEntity, j, blockPos, 0);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.isClient) {
			boolean bl = world.isReceivingRedstonePower(pos);
			if (bl != (Boolean)state.getProperty(field_18533)) {
				if ((Boolean)state.getProperty(field_18531) != bl) {
					state = state.withProperty(field_18531, Boolean.valueOf(bl));
					this.method_11640(null, world, pos, bl);
				}

				world.setBlockState(pos, state.withProperty(field_18533, Boolean.valueOf(bl)), 2);
				if ((Boolean)state.getProperty(field_18534)) {
					world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
				}
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = this.getDefaultState();
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		Direction direction = context.method_16151();
		if (!context.method_16019() && direction.getAxis().isHorizontal()) {
			blockState = blockState.withProperty(FACING, direction).withProperty(field_18532, context.method_16153() > 0.5F ? BlockHalf.TOP : BlockHalf.BOTTOM);
		} else {
			blockState = blockState.withProperty(FACING, context.method_16145().getOpposite())
				.withProperty(field_18532, direction == Direction.UP ? BlockHalf.BOTTOM : BlockHalf.TOP);
		}

		if (context.getWorld().isReceivingRedstonePower(context.getBlockPos())) {
			blockState = blockState.withProperty(field_18531, Boolean.valueOf(true)).withProperty(field_18533, Boolean.valueOf(true));
		}

		return blockState.withProperty(field_18534, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, field_18531, field_18532, field_18533, field_18534);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return (
					direction == Direction.UP && state.getProperty(field_18532) == BlockHalf.TOP
						|| direction == Direction.DOWN && state.getProperty(field_18532) == BlockHalf.BOTTOM
				)
				&& !state.getProperty(field_18531)
			? BlockRenderLayer.SOLID
			: BlockRenderLayer.UNDEFINED;
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if ((Boolean)state.getProperty(field_18534)) {
			world.setBlockState(pos, state.withProperty(field_18534, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getProperty(field_18534) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return !(Boolean)state.getProperty(field_18534) && fluid == Fluids.WATER;
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!(Boolean)state.getProperty(field_18534) && fluidState.getFluid() == Fluids.WATER) {
			if (!world.method_16390()) {
				world.setBlockState(pos, state.withProperty(field_18534, Boolean.valueOf(true)), 3);
				world.method_16340().schedule(pos, fluidState.getFluid(), fluidState.getFluid().method_17778(world));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.getProperty(field_18534)) {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
}
