package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Growable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class class_3720 extends PlantBlock implements Growable, FluidFillable {
	protected static final VoxelShape field_18471 = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

	protected class_3720(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18471;
	}

	@Override
	protected boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
		return Block.isFaceFullSquare(state.getCollisionShape(world, pos), Direction.UP) && state.getBlock() != Blocks.MAGMA_BLOCK;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		return fluidState.matches(FluidTags.WATER) && fluidState.method_17811() == 8 ? super.getPlacementState(context) : null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		BlockState blockState = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		if (!blockState.isAir()) {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
		}

		return blockState;
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		if (!world.isClient && stack.getItem() == Items.SHEARS) {
			player.method_15932(Stats.MINED.method_21429(this));
			player.addExhaustion(0.005F);
			onBlockBreak(world, pos, new ItemStack(this));
		} else {
			super.method_8651(world, player, pos, state, blockEntity, stack);
		}
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
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
	public FluidState getFluidState(BlockState state) {
		return Fluids.WATER.getStill(false);
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		BlockState blockState = Blocks.TALL_SEAGRASS.getDefaultState();
		BlockState blockState2 = blockState.withProperty(class_3730.field_18527, DoubleBlockHalf.UPPER);
		BlockPos blockPos = pos.up();
		if (world.getBlockState(blockPos).getBlock() == Blocks.WATER) {
			world.setBlockState(pos, blockState, 2);
			world.setBlockState(blockPos, blockState2, 2);
		}
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		return false;
	}

	@Override
	public int getLightSubtracted(BlockState state, BlockView world, BlockPos pos) {
		return Blocks.WATER.getDefaultState().method_16885(world, pos);
	}
}
