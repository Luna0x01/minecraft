package net.minecraft.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ChestBlock extends AbstractChestBlock<ChestBlockEntity> implements Waterloggable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final EnumProperty<ChestType> CHEST_TYPE = Properties.CHEST_TYPE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
	protected static final VoxelShape DOUBLE_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
	protected static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>> INVENTORY_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>>() {
		public Optional<Inventory> getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
			return Optional.of(new DoubleInventory(chestBlockEntity, chestBlockEntity2));
		}

		public Optional<Inventory> getFrom(ChestBlockEntity chestBlockEntity) {
			return Optional.of(chestBlockEntity);
		}

		public Optional<Inventory> getFallback() {
			return Optional.empty();
		}
	};
	private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NameableContainerFactory>> NAME_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NameableContainerFactory>>() {
		public Optional<NameableContainerFactory> getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
			final Inventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
			return Optional.of(new NameableContainerFactory() {
				@Nullable
				@Override
				public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
					if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
						chestBlockEntity.checkLootInteraction(playerInventory.player);
						chestBlockEntity2.checkLootInteraction(playerInventory.player);
						return GenericContainer.createGeneric9x6(i, playerInventory, inventory);
					} else {
						return null;
					}
				}

				@Override
				public Text getDisplayName() {
					if (chestBlockEntity.hasCustomName()) {
						return chestBlockEntity.getDisplayName();
					} else {
						return (Text)(chestBlockEntity2.hasCustomName() ? chestBlockEntity2.getDisplayName() : new TranslatableText("container.chestDouble"));
					}
				}
			});
		}

		public Optional<NameableContainerFactory> getFrom(ChestBlockEntity chestBlockEntity) {
			return Optional.of(chestBlockEntity);
		}

		public Optional<NameableContainerFactory> getFallback() {
			return Optional.empty();
		}
	};

	protected ChestBlock(Block.Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier) {
		super(settings, supplier);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.field_11043).with(CHEST_TYPE, ChestType.field_12569).with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	public static DoubleBlockProperties.Type getDoubleBlockType(BlockState blockState) {
		ChestType chestType = blockState.get(CHEST_TYPE);
		if (chestType == ChestType.field_12569) {
			return DoubleBlockProperties.Type.field_21783;
		} else {
			return chestType == ChestType.field_12571 ? DoubleBlockProperties.Type.field_21784 : DoubleBlockProperties.Type.field_21785;
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11456;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		if (blockState2.getBlock() == this && direction.getAxis().isHorizontal()) {
			ChestType chestType = blockState2.get(CHEST_TYPE);
			if (blockState.get(CHEST_TYPE) == ChestType.field_12569
				&& chestType != ChestType.field_12569
				&& blockState.get(FACING) == blockState2.get(FACING)
				&& getFacing(blockState2) == direction.getOpposite()) {
				return blockState.with(CHEST_TYPE, chestType.getOpposite());
			}
		} else if (getFacing(blockState) == direction) {
			return blockState.with(CHEST_TYPE, ChestType.field_12569);
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		if (blockState.get(CHEST_TYPE) == ChestType.field_12569) {
			return SINGLE_SHAPE;
		} else {
			switch (getFacing(blockState)) {
				case field_11043:
				default:
					return DOUBLE_NORTH_SHAPE;
				case field_11035:
					return DOUBLE_SOUTH_SHAPE;
				case field_11039:
					return DOUBLE_WEST_SHAPE;
				case field_11034:
					return DOUBLE_EAST_SHAPE;
			}
		}
	}

	public static Direction getFacing(BlockState blockState) {
		Direction direction = blockState.get(FACING);
		return blockState.get(CHEST_TYPE) == ChestType.field_12574 ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		ChestType chestType = ChestType.field_12569;
		Direction direction = itemPlacementContext.getPlayerFacing().getOpposite();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
		boolean bl = itemPlacementContext.shouldCancelInteraction();
		Direction direction2 = itemPlacementContext.getSide();
		if (direction2.getAxis().isHorizontal() && bl) {
			Direction direction3 = this.getNeighborChestDirection(itemPlacementContext, direction2.getOpposite());
			if (direction3 != null && direction3.getAxis() != direction2.getAxis()) {
				direction = direction3;
				chestType = direction3.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.field_12571 : ChestType.field_12574;
			}
		}

		if (chestType == ChestType.field_12569 && !bl) {
			if (direction == this.getNeighborChestDirection(itemPlacementContext, direction.rotateYClockwise())) {
				chestType = ChestType.field_12574;
			} else if (direction == this.getNeighborChestDirection(itemPlacementContext, direction.rotateYCounterclockwise())) {
				chestType = ChestType.field_12571;
			}
		}

		return this.getDefaultState().with(FACING, direction).with(CHEST_TYPE, chestType).with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(blockState);
	}

	@Nullable
	private Direction getNeighborChestDirection(ItemPlacementContext itemPlacementContext, Direction direction) {
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos().offset(direction));
		return blockState.getBlock() == this && blockState.get(CHEST_TYPE) == ChestType.field_12569 ? blockState.get(FACING) : null;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, blockPos, (Inventory)blockEntity);
				world.updateHorizontalAdjacent(blockPos, this);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return ActionResult.field_5812;
		} else {
			NameableContainerFactory nameableContainerFactory = this.createContainerFactory(blockState, world, blockPos);
			if (nameableContainerFactory != null) {
				playerEntity.openContainer(nameableContainerFactory);
				playerEntity.incrementStat(this.getOpenStat());
			}

			return ActionResult.field_5812;
		}
	}

	protected Stat<Identifier> getOpenStat() {
		return Stats.field_15419.getOrCreateStat(Stats.field_15395);
	}

	@Nullable
	public static Inventory getInventory(ChestBlock chestBlock, BlockState blockState, World world, BlockPos blockPos, boolean bl) {
		return (Inventory)chestBlock.getBlockEntitySource(blockState, world, blockPos, bl).apply(INVENTORY_RETRIEVER).orElse(null);
	}

	@Override
	public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(BlockState blockState, World world, BlockPos blockPos, boolean bl) {
		BiPredicate<IWorld, BlockPos> biPredicate;
		if (bl) {
			biPredicate = (iWorld, blockPosx) -> false;
		} else {
			biPredicate = ChestBlock::isChestBlocked;
		}

		return DoubleBlockProperties.toPropertySource(
			(BlockEntityType<? extends ChestBlockEntity>)this.entityTypeRetriever.get(),
			ChestBlock::getDoubleBlockType,
			ChestBlock::getFacing,
			FACING,
			blockState,
			world,
			blockPos,
			biPredicate
		);
	}

	@Nullable
	@Override
	public NameableContainerFactory createContainerFactory(BlockState blockState, World world, BlockPos blockPos) {
		return (NameableContainerFactory)this.getBlockEntitySource(blockState, world, blockPos, false).apply(NAME_RETRIEVER).orElse(null);
	}

	public static DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Float2FloatFunction> getAnimationProgressRetriever(
		ChestAnimationProgress chestAnimationProgress
	) {
		return new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Float2FloatFunction>() {
			public Float2FloatFunction getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
				return f -> Math.max(chestBlockEntity.getAnimationProgress(f), chestBlockEntity2.getAnimationProgress(f));
			}

			public Float2FloatFunction getFrom(ChestBlockEntity chestBlockEntity) {
				return chestBlockEntity::getAnimationProgress;
			}

			public Float2FloatFunction getFallback() {
				return chestAnimationProgress::getAnimationProgress;
			}
		};
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new ChestBlockEntity();
	}

	public static boolean isChestBlocked(IWorld iWorld, BlockPos blockPos) {
		return hasBlockOnTop(iWorld, blockPos) || hasOcelotOnTop(iWorld, blockPos);
	}

	private static boolean hasBlockOnTop(BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		return blockView.getBlockState(blockPos2).isSimpleFullBlock(blockView, blockPos2);
	}

	private static boolean hasOcelotOnTop(IWorld iWorld, BlockPos blockPos) {
		List<CatEntity> list = iWorld.getNonSpectatingEntities(
			CatEntity.class,
			new Box(
				(double)blockPos.getX(),
				(double)(blockPos.getY() + 1),
				(double)blockPos.getZ(),
				(double)(blockPos.getX() + 1),
				(double)(blockPos.getY() + 2),
				(double)(blockPos.getZ() + 1)
			)
		);
		if (!list.isEmpty()) {
			for (CatEntity catEntity : list) {
				if (catEntity.isSitting()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return Container.calculateComparatorOutput(getInventory(this, blockState, world, blockPos, false));
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, CHEST_TYPE, WATERLOGGED);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
