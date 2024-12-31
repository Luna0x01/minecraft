package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.world.BlockView;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class DispenserBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = FacingBlock.FACING;
	public static final BooleanProperty field_18291 = Properties.TRIGGERED;
	private static final Map<Item, DispenserBehavior> field_18292 = Util.make(
		new Object2ObjectOpenHashMap(), object2ObjectOpenHashMap -> object2ObjectOpenHashMap.defaultReturnValue(new ItemDispenserBehavior())
	);

	public static void method_16665(Itemable itemable, DispenserBehavior dispenserBehavior) {
		field_18292.put(itemable.getItem(), dispenserBehavior);
	}

	protected DispenserBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.NORTH).withProperty(field_18291, Boolean.valueOf(false)));
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return 4;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof DispenserBlockEntity) {
				player.openInventory((DispenserBlockEntity)blockEntity);
				if (blockEntity instanceof DropperBlockEntity) {
					player.method_15928(Stats.INSPECT_DROPPER);
				} else {
					player.method_15928(Stats.INSPECT_DISPENSER);
				}
			}

			return true;
		}
	}

	protected void dispense(World world, BlockPos pos) {
		BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
		DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();
		int i = dispenserBlockEntity.chooseNonEmptySlot();
		if (i < 0) {
			world.syncGlobalEvent(1001, pos, 0);
		} else {
			ItemStack itemStack = dispenserBlockEntity.getInvStack(i);
			DispenserBehavior dispenserBehavior = this.getBehaviorForItem(itemStack);
			if (dispenserBehavior != DispenserBehavior.NOOP) {
				dispenserBlockEntity.setInvStack(i, dispenserBehavior.dispense(blockPointerImpl, itemStack));
			}
		}
	}

	protected DispenserBehavior getBehaviorForItem(ItemStack stack) {
		return (DispenserBehavior)field_18292.get(stack.getItem());
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
		boolean bl2 = (Boolean)state.getProperty(field_18291);
		if (bl && !bl2) {
			world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
			world.setBlockState(pos, state.withProperty(field_18291, Boolean.valueOf(true)), 4);
		} else if (!bl && bl2) {
			world.setBlockState(pos, state.withProperty(field_18291, Boolean.valueOf(false)), 4);
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient) {
			this.dispense(world, pos);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new DispenserBlockEntity();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(FACING, context.method_16020().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof DispenserBlockEntity) {
				((DispenserBlockEntity)blockEntity).method_16835(itemStack.getName());
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof DispenserBlockEntity) {
				ItemScatterer.spawn(world, pos, (DispenserBlockEntity)blockEntity);
				world.updateHorizontalAdjacent(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	public static Position getPosition(BlockPointer pointer) {
		Direction direction = pointer.getBlockState().getProperty(FACING);
		double d = pointer.getX() + 0.7 * (double)direction.getOffsetX();
		double e = pointer.getY() + 0.7 * (double)direction.getOffsetY();
		double f = pointer.getZ() + 0.7 * (double)direction.getOffsetZ();
		return new PositionImpl(d, e, f);
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
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
		builder.method_16928(FACING, field_18291);
	}
}
