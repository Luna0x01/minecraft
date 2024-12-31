package net.minecraft.block;

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
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ChestBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);
	public final int type;

	protected ChestBlock(int i) {
		super(Material.WOOD);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
		this.type = i;
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setBoundingBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getBlockType() {
		return 2;
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		if (view.getBlockState(pos.north()).getBlock() == this) {
			this.setBoundingBox(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
		} else if (view.getBlockState(pos.south()).getBlock() == this) {
			this.setBoundingBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
		} else if (view.getBlockState(pos.west()).getBlock() == this) {
			this.setBoundingBox(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		} else if (view.getBlockState(pos.east()).getBlock() == this) {
			this.setBoundingBox(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
		} else {
			this.setBoundingBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
			Block block = blockState.getBlock();
			Block block2 = blockState2.getBlock();
			Block block3 = blockState3.getBlock();
			Block block4 = blockState4.getBlock();
			if (block != this && block2 != this) {
				boolean bl = block.isFullBlock();
				boolean bl2 = block2.isFullBlock();
				if (block3 == this || block4 == this) {
					BlockPos blockPos2 = block3 == this ? pos.west() : pos.east();
					BlockState blockState7 = world.getBlockState(blockPos2.north());
					BlockState blockState8 = world.getBlockState(blockPos2.south());
					direction = Direction.SOUTH;
					Direction direction4;
					if (block3 == this) {
						direction4 = blockState3.get(FACING);
					} else {
						direction4 = blockState4.get(FACING);
					}

					if (direction4 == Direction.NORTH) {
						direction = Direction.NORTH;
					}

					Block block7 = blockState7.getBlock();
					Block block8 = blockState8.getBlock();
					if ((bl || block7.isFullBlock()) && !bl2 && !block8.isFullBlock()) {
						direction = Direction.SOUTH;
					}

					if ((bl2 || block8.isFullBlock()) && !bl && !block7.isFullBlock()) {
						direction = Direction.NORTH;
					}
				}
			} else {
				BlockPos blockPos = block == this ? pos.north() : pos.south();
				BlockState blockState5 = world.getBlockState(blockPos.west());
				BlockState blockState6 = world.getBlockState(blockPos.east());
				direction = Direction.EAST;
				Direction direction2;
				if (block == this) {
					direction2 = blockState.get(FACING);
				} else {
					direction2 = blockState2.get(FACING);
				}

				if (direction2 == Direction.WEST) {
					direction = Direction.WEST;
				}

				Block block5 = blockState5.getBlock();
				Block block6 = blockState6.getBlock();
				if ((block3.isFullBlock() || block5.isFullBlock()) && !block4.isFullBlock() && !block6.isFullBlock()) {
					direction = Direction.EAST;
				}

				if ((block4.isFullBlock() || block6.isFullBlock()) && !block3.isFullBlock() && !block5.isFullBlock()) {
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

			if (blockState2.getBlock().isFullBlock()) {
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
			if (world.getBlockState(blockPos.offset(direction3)).getBlock().isFullBlock()) {
				direction3 = direction3.getOpposite();
			}

			if (world.getBlockState(blockPos.offset(direction3)).getBlock().isFullBlock()) {
				direction3 = direction3.rotateYClockwise();
			}

			if (world.getBlockState(blockPos.offset(direction3)).getBlock().isFullBlock()) {
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
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		super.neighborUpdate(world, pos, state, block);
		BlockEntity blockEntity = world.getBlockEntity(pos);
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
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			LockableScreenHandlerFactory lockableScreenHandlerFactory = this.createScreenHandlerFactory(world, pos);
			if (lockableScreenHandlerFactory != null) {
				player.openInventory(lockableScreenHandlerFactory);
				if (this.type == 0) {
					player.incrementStat(Stats.CHEST_OPENED);
				} else if (this.type == 1) {
					player.incrementStat(Stats.TRAPPED_CHEST_TRIGGERED);
				}
			}

			return true;
		}
	}

	public LockableScreenHandlerFactory createScreenHandlerFactory(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof ChestBlockEntity)) {
			return null;
		} else {
			LockableScreenHandlerFactory lockableScreenHandlerFactory = (ChestBlockEntity)blockEntity;
			if (this.isChestBlocked(world, pos)) {
				return null;
			} else {
				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					BlockPos blockPos = pos.offset(direction);
					Block block = world.getBlockState(blockPos).getBlock();
					if (block == this) {
						if (this.isChestBlocked(world, blockPos)) {
							return null;
						}

						BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
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
	public boolean emitsRedstonePower() {
		return this.type == 1;
	}

	@Override
	public int getWeakRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		if (!this.emitsRedstonePower()) {
			return 0;
		} else {
			int i = 0;
			BlockEntity blockEntity = view.getBlockEntity(pos);
			if (blockEntity instanceof ChestBlockEntity) {
				i = ((ChestBlockEntity)blockEntity).viewerCount;
			}

			return MathHelper.clamp(i, 0, 15);
		}
	}

	@Override
	public int getStrongRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return facing == Direction.UP ? this.getWeakRedstonePower(view, pos, state, facing) : 0;
	}

	private boolean isChestBlocked(World world, BlockPos pos) {
		return this.isUnderSolidBlock(world, pos) || this.hasCatOnTop(world, pos);
	}

	private boolean isUnderSolidBlock(World world, BlockPos pos) {
		return world.getBlockState(pos.up()).getBlock().isFullCube();
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
	public boolean hasComparatorOutput() {
		return true;
	}

	@Override
	public int getComparatorOutput(World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(this.createScreenHandlerFactory(world, pos));
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
	protected StateManager appendProperties() {
		return new StateManager(this, FACING);
	}
}
