package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.world.World;

public class DispenserBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = DirectionProperty.of("facing");
	public static final BooleanProperty TRIGGERED = BooleanProperty.of("triggered");
	public static final DefaultedRegistry<Item, DispenserBehavior> SPECIAL_ITEMS = new DefaultedRegistry<>(new ItemDispenserBehavior());
	protected Random random = new Random();

	protected DispenserBlock() {
		super(Material.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, false));
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public int getTickRate(World world) {
		return 4;
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		super.onCreation(world, pos, state);
		this.setDirection(world, pos, state);
	}

	private void setDirection(World world, BlockPos pos, BlockState state) {
		if (!world.isClient) {
			Direction direction = state.get(FACING);
			boolean bl = world.getBlockState(pos.north()).getBlock().isFullBlock();
			boolean bl2 = world.getBlockState(pos.south()).getBlock().isFullBlock();
			if (direction == Direction.NORTH && bl && !bl2) {
				direction = Direction.SOUTH;
			} else if (direction == Direction.SOUTH && bl2 && !bl) {
				direction = Direction.NORTH;
			} else {
				boolean bl3 = world.getBlockState(pos.west()).getBlock().isFullBlock();
				boolean bl4 = world.getBlockState(pos.east()).getBlock().isFullBlock();
				if (direction == Direction.WEST && bl3 && !bl4) {
					direction = Direction.EAST;
				} else if (direction == Direction.EAST && bl4 && !bl3) {
					direction = Direction.WEST;
				}
			}

			world.setBlockState(pos, state.with(FACING, direction).with(TRIGGERED, false), 2);
		}
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof DispenserBlockEntity) {
				player.openInventory((DispenserBlockEntity)blockEntity);
				if (blockEntity instanceof DropperBlockEntity) {
					player.incrementStat(Stats.INTERACTIONS_WITH_DROPPER);
				} else {
					player.incrementStat(Stats.INTERACTIONS_WITH_DISPENSER);
				}
			}

			return true;
		}
	}

	protected void dispense(World world, BlockPos pos) {
		BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
		DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();
		if (dispenserBlockEntity != null) {
			int i = dispenserBlockEntity.chooseNonEmptySlot();
			if (i < 0) {
				world.syncGlobalEvent(1001, pos, 0);
			} else {
				ItemStack itemStack = dispenserBlockEntity.getInvStack(i);
				DispenserBehavior dispenserBehavior = this.getBehaviorForItem(itemStack);
				if (dispenserBehavior != DispenserBehavior.INSTANCE) {
					ItemStack itemStack2 = dispenserBehavior.dispense(blockPointerImpl, itemStack);
					dispenserBlockEntity.setInvStack(i, itemStack2.count <= 0 ? null : itemStack2);
				}
			}
		}
	}

	protected DispenserBehavior getBehaviorForItem(ItemStack stack) {
		return SPECIAL_ITEMS.get(stack == null ? null : stack.getItem());
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
		boolean bl2 = (Boolean)state.get(TRIGGERED);
		if (bl && !bl2) {
			world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
			world.setBlockState(pos, state.with(TRIGGERED, true), 4);
		} else if (!bl && bl2) {
			world.setBlockState(pos, state.with(TRIGGERED, false), 4);
		}
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			this.dispense(world, pos);
		}
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new DispenserBlockEntity();
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, PistonBlock.getDirectionFromEntity(world, pos, entity)).with(TRIGGERED, false);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos, state.with(FACING, PistonBlock.getDirectionFromEntity(world, pos, placer)), 2);
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof DispenserBlockEntity) {
				((DispenserBlockEntity)blockEntity).setCustomName(itemStack.getCustomName());
			}
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof DispenserBlockEntity) {
			ItemScatterer.spawn(world, pos, (DispenserBlockEntity)blockEntity);
			world.updateHorizontalAdjacent(pos, this);
		}

		super.onBreaking(world, pos, state);
	}

	public static Position getPosition(BlockPointer pointer) {
		Direction direction = getDirection(pointer.getBlockStateData());
		double d = pointer.getX() + 0.7 * (double)direction.getOffsetX();
		double e = pointer.getY() + 0.7 * (double)direction.getOffsetY();
		double f = pointer.getZ() + 0.7 * (double)direction.getOffsetZ();
		return new PositionImpl(d, e, f);
	}

	public static Direction getDirection(int data) {
		return Direction.getById(data & 7);
	}

	@Override
	public boolean hasComparatorOutput() {
		return true;
	}

	@Override
	public int getComparatorOutput(World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public int getBlockType() {
		return 3;
	}

	@Override
	public BlockState getRenderState(BlockState state) {
		return this.getDefaultState().with(FACING, Direction.SOUTH);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, getDirection(data)).with(TRIGGERED, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getId();
		if ((Boolean)state.get(TRIGGERED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, TRIGGERED);
	}
}
