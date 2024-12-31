package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4342;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Itemable;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class EnderChestBlock extends BlockWithEntity implements FluidDrainable, FluidFillable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty field_18313 = Properties.WATERLOGGED;
	protected static final VoxelShape field_18314 = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

	protected EnderChestBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.NORTH).withProperty(field_18313, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18314;
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
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Blocks.OBSIDIAN;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 8;
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		return this.getDefaultState()
			.withProperty(FACING, context.method_16145().getOpposite())
			.withProperty(field_18313, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		EnderChestInventory enderChestInventory = player.getEnderChestInventory();
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (enderChestInventory == null || !(blockEntity instanceof EnderChestBlockEntity)) {
			return true;
		} else if (world.getBlockState(pos.up()).method_16907()) {
			return true;
		} else if (world.isClient) {
			return true;
		} else {
			enderChestInventory.setBlockEntity((EnderChestBlockEntity)blockEntity);
			player.openInventory(enderChestInventory);
			player.method_15928(Stats.OPEN_ENDERCHEST);
			return true;
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new EnderChestBlockEntity();
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		for (int i = 0; i < 3; i++) {
			int j = random.nextInt(2) * 2 - 1;
			int k = random.nextInt(2) * 2 - 1;
			double d = (double)pos.getX() + 0.5 + 0.25 * (double)j;
			double e = (double)((float)pos.getY() + random.nextFloat());
			double f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
			double g = (double)(random.nextFloat() * (float)j);
			double h = ((double)random.nextFloat() - 0.5) * 0.125;
			double l = (double)(random.nextFloat() * (float)k);
			world.method_16343(class_4342.field_21361, d, e, f, g, h, l);
		}
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
		builder.method_16928(FACING, field_18313);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if ((Boolean)state.getProperty(field_18313)) {
			world.setBlockState(pos, state.withProperty(field_18313, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getProperty(field_18313) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return !(Boolean)state.getProperty(field_18313) && fluid == Fluids.WATER;
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!(Boolean)state.getProperty(field_18313) && fluidState.getFluid() == Fluids.WATER) {
			if (!world.method_16390()) {
				world.setBlockState(pos, state.withProperty(field_18313, Boolean.valueOf(true)), 3);
				world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.getProperty(field_18313)) {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
