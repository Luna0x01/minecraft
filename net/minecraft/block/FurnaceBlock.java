package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4342;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FurnaceBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty field_18348 = RedstoneTorchBlock.field_18451;

	protected FurnaceBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.NORTH).withProperty(field_18348, Boolean.valueOf(false)));
	}

	@Override
	public int getLuminance(BlockState state) {
		return state.getProperty(field_18348) ? super.getLuminance(state) : 0;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof FurnaceBlockEntity) {
				player.openInventory((FurnaceBlockEntity)blockEntity);
				player.method_15928(Stats.INTERACT_WITH_FURNACE);
			}

			return true;
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new FurnaceBlockEntity();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(FACING, context.method_16145().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof FurnaceBlockEntity) {
				((FurnaceBlockEntity)blockEntity).method_16812(itemStack.getName());
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof FurnaceBlockEntity) {
				ItemScatterer.spawn(world, pos, (FurnaceBlockEntity)blockEntity);
				world.updateHorizontalAdjacent(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
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
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.getProperty(field_18348)) {
			double d = (double)pos.getX() + 0.5;
			double e = (double)pos.getY();
			double f = (double)pos.getZ() + 0.5;
			if (random.nextDouble() < 0.1) {
				world.playSound(d, e, f, Sounds.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}

			Direction direction = state.getProperty(FACING);
			Direction.Axis axis = direction.getAxis();
			double g = 0.52;
			double h = random.nextDouble() * 0.6 - 0.3;
			double i = axis == Direction.Axis.X ? (double)direction.getOffsetX() * 0.52 : h;
			double j = random.nextDouble() * 6.0 / 16.0;
			double k = axis == Direction.Axis.Z ? (double)direction.getOffsetZ() * 0.52 : h;
			world.method_16343(class_4342.field_21363, d + i, e + j, f + k, 0.0, 0.0, 0.0);
			world.method_16343(class_4342.field_21399, d + i, e + j, f + k, 0.0, 0.0, 0.0);
		}
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
		builder.method_16928(FACING, field_18348);
	}
}
