package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Growable;
import net.minecraft.block.PlantBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class class_3719 extends PlantBlock implements Growable, FluidDrainable, FluidFillable {
	public static final IntProperty field_18465 = Properties.PICKLES;
	public static final BooleanProperty field_18466 = Properties.WATERLOGGED;
	protected static final VoxelShape field_18467 = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
	protected static final VoxelShape field_18468 = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
	protected static final VoxelShape field_18469 = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
	protected static final VoxelShape field_18470 = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

	protected class_3719(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18465, Integer.valueOf(1)).withProperty(field_18466, Boolean.valueOf(true)));
	}

	@Override
	public int getLuminance(BlockState state) {
		return this.method_16735(state) ? 0 : super.getLuminance(state) + 3 * (Integer)state.getProperty(field_18465);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		if (blockState.getBlock() == this) {
			return blockState.withProperty(field_18465, Integer.valueOf(Math.min(4, (Integer)blockState.getProperty(field_18465) + 1)));
		} else {
			FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
			boolean bl = fluidState.matches(FluidTags.WATER) && fluidState.method_17811() == 8;
			return super.getPlacementState(context).withProperty(field_18466, Boolean.valueOf(bl));
		}
	}

	private boolean method_16735(BlockState blockState) {
		return !(Boolean)blockState.getProperty(field_18466);
	}

	@Override
	protected boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
		return !state.getCollisionShape(world, pos).getFace(Direction.UP).isEmpty();
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		return this.canPlantOnTop(world.getBlockState(blockPos), world, blockPos);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (!state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			if ((Boolean)state.getProperty(field_18466)) {
				world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
			}

			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		}
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext.getItemStack().getItem() == this.getItem() && state.getProperty(field_18465) < 4
			? true
			: super.canReplace(state, itemPlacementContext);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		switch (state.getProperty(field_18465)) {
			case 1:
			default:
				return field_18467;
			case 2:
				return field_18468;
			case 3:
				return field_18469;
			case 4:
				return field_18470;
		}
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if ((Boolean)state.getProperty(field_18466)) {
			world.setBlockState(pos, state.withProperty(field_18466, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getProperty(field_18466) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return !(Boolean)state.getProperty(field_18466) && fluid == Fluids.WATER;
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!(Boolean)state.getProperty(field_18466) && fluidState.getFluid() == Fluids.WATER) {
			if (!world.method_16390()) {
				world.setBlockState(pos, state.withProperty(field_18466, Boolean.valueOf(true)), 3);
				world.method_16340().schedule(pos, fluidState.getFluid(), fluidState.getFluid().method_17778(world));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18465, field_18466);
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return (Integer)state.getProperty(field_18465);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		if (!this.method_16735(state) && world.getBlockState(pos.down()).isIn(BlockTags.CORAL_BLOCKS)) {
			int i = 5;
			int j = 1;
			int k = 2;
			int l = 0;
			int m = pos.getX() - 2;
			int n = 0;

			for (int o = 0; o < 5; o++) {
				for (int p = 0; p < j; p++) {
					int q = 2 + pos.getY() - 1;

					for (int r = q - 2; r < q; r++) {
						BlockPos blockPos = new BlockPos(m + o, r, pos.getZ() - n + p);
						if (blockPos != pos && random.nextInt(6) == 0 && world.getBlockState(blockPos).getBlock() == Blocks.WATER) {
							BlockState blockState = world.getBlockState(blockPos.down());
							if (blockState.isIn(BlockTags.CORAL_BLOCKS)) {
								world.setBlockState(blockPos, Blocks.SEA_PICKLE.getDefaultState().withProperty(field_18465, Integer.valueOf(random.nextInt(4) + 1)), 3);
							}
						}
					}
				}

				if (l < 2) {
					j += 2;
					n++;
				} else {
					j -= 2;
					n--;
				}

				l++;
			}

			world.setBlockState(pos, state.withProperty(field_18465, Integer.valueOf(4)), 2);
		}
	}
}
