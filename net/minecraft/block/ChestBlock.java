package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ChestBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	protected static final Box field_12616 = new Box(0.0625, 0.0, 0.0, 0.9375, 0.875, 0.9375);
	protected static final Box field_12617 = new Box(0.0625, 0.0, 0.0625, 0.9375, 0.875, 1.0);
	protected static final Box field_12618 = new Box(0.0, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
	protected static final Box field_12619 = new Box(0.0625, 0.0, 0.0625, 1.0, 0.875, 0.9375);
	protected static final Box field_12620 = new Box(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
	public final ChestBlock.Type field_12621;

	protected ChestBlock(ChestBlock.Type type) {
		super(Material.WOOD);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
		this.field_12621 = type;
		this.setItemGroup(type == ChestBlock.Type.TRAP ? ItemGroup.REDSTONE : ItemGroup.DECORATIONS);
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		if (view.getBlockState(pos.north()).getBlock() == this) {
			return field_12616;
		} else if (view.getBlockState(pos.south()).getBlock() == this) {
			return field_12617;
		} else if (view.getBlockState(pos.west()).getBlock() == this) {
			return field_12618;
		} else {
			return view.getBlockState(pos.east()).getBlock() == this ? field_12619 : field_12620;
		}
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		this.getNearbyChest(world, pos, state);

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.getBlock() == this) {
				this.getNearbyChest(world, blockPos, blockState);
			}
		}
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, entity.getHorizontalDirection());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		Direction direction = Direction.fromHorizontal(MathHelper.floor((double)(placer.yaw * 4.0F / 360.0F) + 0.5) & 3).getOpposite();
		state = state.with(FACING, direction);
		BlockPos blockPos = pos.north();
		BlockPos blockPos2 = pos.south();
		BlockPos blockPos3 = pos.west();
		BlockPos blockPos4 = pos.east();
		boolean bl = this == world.getBlockState(blockPos).getBlock();
		boolean bl2 = this == world.getBlockState(blockPos2).getBlock();
		boolean bl3 = this == world.getBlockState(blockPos3).getBlock();
		boolean bl4 = this == world.getBlockState(blockPos4).getBlock();
		if (!bl && !bl2 && !bl3 && !bl4) {
			world.setBlockState(pos, state, 3);
		} else if (direction.getAxis() != Direction.Axis.X || !bl && !bl2) {
			if (direction.getAxis() == Direction.Axis.Z && (bl3 || bl4)) {
				if (bl3) {
					world.setBlockState(blockPos3, state, 3);
				} else {
					world.setBlockState(blockPos4, state, 3);
				}

				world.setBlockState(pos, state, 3);
			}
		} else {
			if (bl) {
				world.setBlockState(blockPos, state, 3);
			} else {
				world.setBlockState(blockPos2, state, 3);
			}

			world.setBlockState(pos, state, 3);
		}

		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity)blockEntity).setTranslationKeyName(itemStack.getCustomName());
			}
		}
	}

	public BlockState getNearbyChest(World world, BlockPos pos, BlockState state) {
		if (world.isClient) {
			return state;
		} else {
			BlockState blockState = world.getBlockState(pos.north());
			BlockState blockState2 = world.getBlockState(pos.south());
			BlockState blockState3 = world.getBlockState(pos.west());
			BlockState blockState4 = world.getBlockState(pos.east());
			Direction direction = state.get(FACING);
			if (blockState.getBlock() != this && blockState2.getBlock() != this) {
				boolean bl = blockState.isFullBlock();
				boolean bl2 = blockState2.isFullBlock();
				if (blockState3.getBlock() == this || blockState4.getBlock() == this) {
					BlockPos blockPos2 = blockState3.getBlock() == this ? pos.west() : pos.east();
					BlockState blockState7 = world.getBlockState(blockPos2.north());
					BlockState blockState8 = world.getBlockState(blockPos2.south());
					direction = Direction.SOUTH;
					Direction direction4;
					if (blockState3.getBlock() == this) {
						direction4 = blockState3.get(FACING);
					} else {
						direction4 = blockState4.get(FACING);
					}

					if (direction4 == Direction.NORTH) {
						direction = Direction.NORTH;
					}

					if ((bl || blockState7.isFullBlock()) && !bl2 && !blockState8.isFullBlock()) {
						direction = Direction.SOUTH;
					}

					if ((bl2 || blockState8.isFullBlock()) && !bl && !blockState7.isFullBlock()) {
						direction = Direction.NORTH;
					}
				}
			} else {
				BlockPos blockPos = blockState.getBlock() == this ? pos.north() : pos.south();
				BlockState blockState5 = world.getBlockState(blockPos.west());
				BlockState blockState6 = world.getBlockState(blockPos.east());
				direction = Direction.EAST;
				Direction direction2;
				if (blockState.getBlock() == this) {
					direction2 = blockState.get(FACING);
				} else {
					direction2 = blockState2.get(FACING);
				}

				if (direction2 == Direction.WEST) {
					direction = Direction.WEST;
				}

				if ((blockState3.isFullBlock() || blockState5.isFullBlock()) && !blockState4.isFullBlock() && !blockState6.isFullBlock()) {
					direction = Direction.EAST;
				}

				if ((blockState4.isFullBlock() || blockState6.isFullBlock()) && !blockState3.isFullBlock() && !blockState5.isFullBlock()) {
					direction = Direction.WEST;
				}
			}

			state = state.with(FACING, direction);
			world.setBlockState(pos, state, 3);
			return state;
		}
	}

	public BlockState changeFacing(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = null;

		for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
			BlockState blockState2 = world.getBlockState(blockPos.offset(direction2));
			if (blockState2.getBlock() == this) {
				return blockState;
			}

			if (blockState2.isFullBlock()) {
				if (direction != null) {
					direction = null;
					break;
				}

				direction = direction2;
			}
		}

		if (direction != null) {
			return blockState.with(FACING, direction.getOpposite());
		} else {
			Direction direction3 = blockState.get(FACING);
			if (world.getBlockState(blockPos.offset(direction3)).isFullBlock()) {
				direction3 = direction3.getOpposite();
			}

			if (world.getBlockState(blockPos.offset(direction3)).isFullBlock()) {
				direction3 = direction3.rotateYClockwise();
			}

			if (world.getBlockState(blockPos.offset(direction3)).isFullBlock()) {
				direction3 = direction3.getOpposite();
			}

			return blockState.with(FACING, direction3);
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		int i = 0;
		BlockPos blockPos = pos.west();
		BlockPos blockPos2 = pos.east();
		BlockPos blockPos3 = pos.north();
		BlockPos blockPos4 = pos.south();
		if (world.getBlockState(blockPos).getBlock() == this) {
			if (this.isSurroundedBySameType(world, blockPos)) {
				return false;
			}

			i++;
		}

		if (world.getBlockState(blockPos2).getBlock() == this) {
			if (this.isSurroundedBySameType(world, blockPos2)) {
				return false;
			}

			i++;
		}

		if (world.getBlockState(blockPos3).getBlock() == this) {
			if (this.isSurroundedBySameType(world, blockPos3)) {
				return false;
			}

			i++;
		}

		if (world.getBlockState(blockPos4).getBlock() == this) {
			if (this.isSurroundedBySameType(world, blockPos4)) {
				return false;
			}

			i++;
		}

		return i <= 1;
	}

	private boolean isSurroundedBySameType(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() != this) {
			return false;
		} else {
			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (world.getBlockState(pos.offset(direction)).getBlock() == this) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		super.method_8641(blockState, world, blockPos, block);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof ChestBlockEntity) {
			blockEntity.resetBlock();
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof Inventory) {
			ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
			world.updateHorizontalAdjacent(pos, this);
		}

		super.onBreaking(world, pos, state);
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		if (world.isClient) {
			return true;
		} else {
			LockableScreenHandlerFactory lockableScreenHandlerFactory = this.method_11583(world, blockPos);
			if (lockableScreenHandlerFactory != null) {
				playerEntity.openInventory(lockableScreenHandlerFactory);
				if (this.field_12621 == ChestBlock.Type.BASIC) {
					playerEntity.incrementStat(Stats.CHEST_OPENED);
				} else if (this.field_12621 == ChestBlock.Type.TRAP) {
					playerEntity.incrementStat(Stats.TRAPPED_CHEST_TRIGGERED);
				}
			}

			return true;
		}
	}

	@Nullable
	public LockableScreenHandlerFactory method_11583(World world, BlockPos blockPos) {
		return this.method_8702(world, blockPos, false);
	}

	@Nullable
	public LockableScreenHandlerFactory method_8702(World world, BlockPos blockPos, boolean bl) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (!(blockEntity instanceof ChestBlockEntity)) {
			return null;
		} else {
			LockableScreenHandlerFactory lockableScreenHandlerFactory = (ChestBlockEntity)blockEntity;
			if (!bl && this.isChestBlocked(world, blockPos)) {
				return null;
			} else {
				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					BlockPos blockPos2 = blockPos.offset(direction);
					Block block = world.getBlockState(blockPos2).getBlock();
					if (block == this) {
						if (this.isChestBlocked(world, blockPos2)) {
							return null;
						}

						BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);
						if (blockEntity2 instanceof ChestBlockEntity) {
							if (direction != Direction.WEST && direction != Direction.NORTH) {
								lockableScreenHandlerFactory = new DoubleInventory("container.chestDouble", lockableScreenHandlerFactory, (ChestBlockEntity)blockEntity2);
							} else {
								lockableScreenHandlerFactory = new DoubleInventory("container.chestDouble", (ChestBlockEntity)blockEntity2, lockableScreenHandlerFactory);
							}
						}
					}
				}

				return lockableScreenHandlerFactory;
			}
		}
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new ChestBlockEntity();
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return this.field_12621 == ChestBlock.Type.TRAP;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!state.emitsRedstonePower()) {
			return 0;
		} else {
			int i = 0;
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ChestBlockEntity) {
				i = ((ChestBlockEntity)blockEntity).viewerCount;
			}

			return MathHelper.clamp(i, 0, 15);
		}
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return direction == Direction.UP ? state.getWeakRedstonePower(world, pos, direction) : 0;
	}

	private boolean isChestBlocked(World world, BlockPos pos) {
		return this.isUnderSolidBlock(world, pos) || this.hasCatOnTop(world, pos);
	}

	private boolean isUnderSolidBlock(World world, BlockPos pos) {
		return world.getBlockState(pos.up()).method_11734();
	}

	private boolean hasCatOnTop(World world, BlockPos pos) {
		for (Entity entity : world.getEntitiesInBox(
			OcelotEntity.class,
			new Box((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))
		)) {
			OcelotEntity ocelotEntity = (OcelotEntity)entity;
			if (ocelotEntity.isSitting()) {
				return true;
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
		return ScreenHandler.calculateComparatorOutput(this.method_11583(world, pos));
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

	public static enum Type {
		BASIC,
		TRAP;
	}
}
