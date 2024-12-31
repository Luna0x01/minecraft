package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.HopperProvider;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HopperBlock extends BlockWithEntity {
	public static final DirectionProperty field_18353 = Properties.HOPPER_FACING;
	public static final BooleanProperty field_18354 = Properties.ENABLED;
	private static final VoxelShape field_18355 = Block.createCuboidShape(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape field_18356 = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
	private static final VoxelShape field_18357 = VoxelShapes.union(field_18356, field_18355);
	private static final VoxelShape field_18358 = VoxelShapes.combineAndSimplify(field_18357, HopperProvider.field_18637, BooleanBiFunction.ONLY_FIRST);
	private static final VoxelShape field_18359 = VoxelShapes.union(field_18358, Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
	private static final VoxelShape field_18360 = VoxelShapes.union(field_18358, Block.createCuboidShape(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
	private static final VoxelShape field_18361 = VoxelShapes.union(field_18358, Block.createCuboidShape(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
	private static final VoxelShape field_18362 = VoxelShapes.union(field_18358, Block.createCuboidShape(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
	private static final VoxelShape field_18363 = VoxelShapes.union(field_18358, Block.createCuboidShape(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
	private static final VoxelShape field_18364 = HopperProvider.field_18637;
	private static final VoxelShape field_18365 = VoxelShapes.union(HopperProvider.field_18637, Block.createCuboidShape(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
	private static final VoxelShape field_18366 = VoxelShapes.union(HopperProvider.field_18637, Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
	private static final VoxelShape field_18367 = VoxelShapes.union(HopperProvider.field_18637, Block.createCuboidShape(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
	private static final VoxelShape field_18352 = VoxelShapes.union(HopperProvider.field_18637, Block.createCuboidShape(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));

	public HopperBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18353, Direction.DOWN).withProperty(field_18354, Boolean.valueOf(true)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		switch ((Direction)state.getProperty(field_18353)) {
			case DOWN:
				return field_18359;
			case NORTH:
				return field_18361;
			case SOUTH:
				return field_18362;
			case WEST:
				return field_18363;
			case EAST:
				return field_18360;
			default:
				return field_18358;
		}
	}

	@Override
	public VoxelShape getRayTraceShape(BlockState state, BlockView world, BlockPos pos) {
		switch ((Direction)state.getProperty(field_18353)) {
			case DOWN:
				return field_18364;
			case NORTH:
				return field_18366;
			case SOUTH:
				return field_18367;
			case WEST:
				return field_18352;
			case EAST:
				return field_18365;
			default:
				return HopperProvider.field_18637;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction direction = context.method_16151().getOpposite();
		return this.getDefaultState()
			.withProperty(field_18353, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction)
			.withProperty(field_18354, Boolean.valueOf(true));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new HopperBlockEntity();
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof HopperBlockEntity) {
				((HopperBlockEntity)blockEntity).method_16835(itemStack.getName());
			}
		}
	}

	@Override
	public boolean method_11568(BlockState state) {
		return true;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (oldState.getBlock() != state.getBlock()) {
			this.updateEnabled(world, pos, state);
		}
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof HopperBlockEntity) {
				player.openInventory((HopperBlockEntity)blockEntity);
				player.method_15928(Stats.INSPECT_HOPPER);
			}

			return true;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		this.updateEnabled(world, pos, state);
	}

	private void updateEnabled(World world, BlockPos pos, BlockState state) {
		boolean bl = !world.isReceivingRedstonePower(pos);
		if (bl != (Boolean)state.getProperty(field_18354)) {
			world.setBlockState(pos, state.withProperty(field_18354, Boolean.valueOf(bl)), 4);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof HopperBlockEntity) {
				ItemScatterer.spawn(world, pos, (HopperBlockEntity)blockEntity);
				world.updateHorizontalAdjacent(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
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
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(field_18353, rotation.rotate(state.getProperty(field_18353)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(field_18353)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18353, field_18354);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction == Direction.UP ? BlockRenderLayer.BOWL : BlockRenderLayer.UNDEFINED;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof HopperBlockEntity) {
			((HopperBlockEntity)blockEntity).method_16822(entity);
		}
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
