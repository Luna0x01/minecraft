package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class TrapdoorBlock extends HorizontalFacingBlock implements Waterloggable {
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final int field_31266 = 3;
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape OPEN_BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
	protected static final VoxelShape OPEN_TOP_SHAPE = Block.createCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);

	protected TrapdoorBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(OPEN, Boolean.valueOf(false))
				.with(HALF, BlockHalf.BOTTOM)
				.with(POWERED, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (!(Boolean)state.get(OPEN)) {
			return state.get(HALF) == BlockHalf.TOP ? OPEN_TOP_SHAPE : OPEN_BOTTOM_SHAPE;
		} else {
			switch ((Direction)state.get(FACING)) {
				case NORTH:
				default:
					return NORTH_SHAPE;
				case SOUTH:
					return SOUTH_SHAPE;
				case WEST:
					return WEST_SHAPE;
				case EAST:
					return EAST_SHAPE;
			}
		}
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		switch (type) {
			case LAND:
				return (Boolean)state.get(OPEN);
			case WATER:
				return (Boolean)state.get(WATERLOGGED);
			case AIR:
				return (Boolean)state.get(OPEN);
			default:
				return false;
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (this.material == Material.METAL) {
			return ActionResult.PASS;
		} else {
			state = state.cycle(OPEN);
			world.setBlockState(pos, state, 2);
			if ((Boolean)state.get(WATERLOGGED)) {
				world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}

			this.playToggleSound(player, world, pos, (Boolean)state.get(OPEN));
			return ActionResult.success(world.isClient);
		}
	}

	protected void playToggleSound(@Nullable PlayerEntity player, World world, BlockPos pos, boolean open) {
		if (open) {
			int i = this.material == Material.METAL ? 1037 : 1007;
			world.syncWorldEvent(player, i, pos, 0);
		} else {
			int j = this.material == Material.METAL ? 1036 : 1013;
			world.syncWorldEvent(player, j, pos, 0);
		}

		world.emitGameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			boolean bl = world.isReceivingRedstonePower(pos);
			if (bl != (Boolean)state.get(POWERED)) {
				if ((Boolean)state.get(OPEN) != bl) {
					state = state.with(OPEN, Boolean.valueOf(bl));
					this.playToggleSound(null, world, pos, bl);
				}

				world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl)), 2);
				if ((Boolean)state.get(WATERLOGGED)) {
					world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
				}
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = this.getDefaultState();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		Direction direction = ctx.getSide();
		if (!ctx.canReplaceExisting() && direction.getAxis().isHorizontal()) {
			blockState = blockState.with(FACING, direction).with(HALF, ctx.getHitPos().y - (double)ctx.getBlockPos().getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM);
		} else {
			blockState = blockState.with(FACING, ctx.getPlayerFacing().getOpposite()).with(HALF, direction == Direction.UP ? BlockHalf.BOTTOM : BlockHalf.TOP);
		}

		if (ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())) {
			blockState = blockState.with(OPEN, Boolean.valueOf(true)).with(POWERED, Boolean.valueOf(true));
		}

		return blockState.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
}
