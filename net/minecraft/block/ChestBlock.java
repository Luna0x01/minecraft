package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_4472;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ChestBlock extends BlockWithEntity implements FluidDrainable, FluidFillable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final EnumProperty<ChestType> CHEST_TYPE = Properties.CHEST_TYPE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
	protected static final VoxelShape DOUBLE_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
	protected static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

	protected ChestBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(FACING, Direction.NORTH)
				.withProperty(CHEST_TYPE, ChestType.SINGLE)
				.withProperty(WATERLOGGED, Boolean.valueOf(false))
		);
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
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.getProperty(WATERLOGGED)) {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
		}

		if (neighborState.getBlock() == this && direction.getAxis().isHorizontal()) {
			ChestType chestType = neighborState.getProperty(CHEST_TYPE);
			if (state.getProperty(CHEST_TYPE) == ChestType.SINGLE
				&& chestType != ChestType.SINGLE
				&& state.getProperty(FACING) == neighborState.getProperty(FACING)
				&& getFacing(neighborState) == direction.getOpposite()) {
				return state.withProperty(CHEST_TYPE, chestType.getOpposite());
			}
		} else if (getFacing(state) == direction) {
			return state.withProperty(CHEST_TYPE, ChestType.SINGLE);
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		if (state.getProperty(CHEST_TYPE) == ChestType.SINGLE) {
			return SINGLE_SHAPE;
		} else {
			switch (getFacing(state)) {
				case NORTH:
				default:
					return DOUBLE_NORTH_SHAPE;
				case SOUTH:
					return DOUBLE_SOUTH_SHAPE;
				case WEST:
					return DOUBLE_WEST_SHAPE;
				case EAST:
					return DOUBLE_EAST_SHAPE;
			}
		}
	}

	public static Direction getFacing(BlockState state) {
		Direction direction = state.getProperty(FACING);
		return state.getProperty(CHEST_TYPE) == ChestType.LEFT ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		ChestType chestType = ChestType.SINGLE;
		Direction direction = context.method_16145().getOpposite();
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		boolean bl = context.method_16146();
		Direction direction2 = context.method_16151();
		if (direction2.getAxis().isHorizontal() && bl) {
			Direction direction3 = this.getNeighborChestDirection(context, direction2.getOpposite());
			if (direction3 != null && direction3.getAxis() != direction2.getAxis()) {
				direction = direction3;
				chestType = direction3.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
			}
		}

		if (chestType == ChestType.SINGLE && !bl) {
			if (direction == this.getNeighborChestDirection(context, direction.rotateYClockwise())) {
				chestType = ChestType.LEFT;
			} else if (direction == this.getNeighborChestDirection(context, direction.rotateYCounterclockwise())) {
				chestType = ChestType.RIGHT;
			}
		}

		return this.getDefaultState()
			.withProperty(FACING, direction)
			.withProperty(CHEST_TYPE, chestType)
			.withProperty(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
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
				world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	private Direction getNeighborChestDirection(ItemPlacementContext context, Direction direction) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos().offset(direction));
		return blockState.getBlock() == this && blockState.getProperty(CHEST_TYPE) == ChestType.SINGLE ? blockState.getProperty(FACING) : null;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity)blockEntity).method_16835(itemStack.getName());
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
				world.updateHorizontalAdjacent(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else {
			LockableScreenHandlerFactory lockableScreenHandlerFactory = this.getInventory(state, world, pos, false);
			if (lockableScreenHandlerFactory != null) {
				player.openInventory(lockableScreenHandlerFactory);
				player.method_15932(this.getOpenStat());
			}

			return true;
		}
	}

	protected class_4472<Identifier> getOpenStat() {
		return Stats.CUSTOM.method_21429(Stats.OPEN_CHEST);
	}

	@Nullable
	public LockableScreenHandlerFactory getInventory(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof ChestBlockEntity)) {
			return null;
		} else if (!ignoreBlocked && this.isChestBlocked(world, pos)) {
			return null;
		} else {
			LockableScreenHandlerFactory lockableScreenHandlerFactory = (ChestBlockEntity)blockEntity;
			ChestType chestType = state.getProperty(CHEST_TYPE);
			if (chestType == ChestType.SINGLE) {
				return lockableScreenHandlerFactory;
			} else {
				BlockPos blockPos = pos.offset(getFacing(state));
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() == this) {
					ChestType chestType2 = blockState.getProperty(CHEST_TYPE);
					if (chestType2 != ChestType.SINGLE && chestType != chestType2 && blockState.getProperty(FACING) == state.getProperty(FACING)) {
						if (!ignoreBlocked && this.isChestBlocked(world, blockPos)) {
							return null;
						}

						BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
						if (blockEntity2 instanceof ChestBlockEntity) {
							LockableScreenHandlerFactory lockableScreenHandlerFactory2 = chestType == ChestType.RIGHT
								? lockableScreenHandlerFactory
								: (LockableScreenHandlerFactory)blockEntity2;
							LockableScreenHandlerFactory lockableScreenHandlerFactory3 = chestType == ChestType.RIGHT
								? (LockableScreenHandlerFactory)blockEntity2
								: lockableScreenHandlerFactory;
							lockableScreenHandlerFactory = new DoubleInventory(
								new TranslatableText("container.chestDouble"), lockableScreenHandlerFactory2, lockableScreenHandlerFactory3
							);
						}
					}
				}

				return lockableScreenHandlerFactory;
			}
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new ChestBlockEntity();
	}

	private boolean isChestBlocked(World world, BlockPos pos) {
		return this.hasBlockOnTop(world, pos) || this.hasCatOnTop(world, pos);
	}

	private boolean hasBlockOnTop(BlockView world, BlockPos pos) {
		return world.getBlockState(pos.up()).method_16907();
	}

	private boolean hasCatOnTop(World world, BlockPos pos) {
		List<OcelotEntity> list = world.getEntitiesInBox(
			OcelotEntity.class,
			new Box((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))
		);
		if (!list.isEmpty()) {
			for (OcelotEntity ocelotEntity : list) {
				if (ocelotEntity.isSitting()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(this.getInventory(state, world, pos, false));
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
		builder.method_16928(FACING, CHEST_TYPE, WATERLOGGED);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
