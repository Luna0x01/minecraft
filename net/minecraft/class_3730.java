package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class class_3730 extends class_3721 implements FluidFillable {
	public static final EnumProperty<DoubleBlockHalf> field_18527 = class_3721.field_18472;
	protected static final VoxelShape field_18528 = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

	public class_3730(Block block, Block.Builder builder) {
		super(block, builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18528;
	}

	@Override
	protected boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
		return Block.isFaceFullSquare(state.getCollisionShape(world, pos), Direction.UP) && state.getBlock() != Blocks.MAGMA_BLOCK;
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(Blocks.SEAGRASS);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = super.getPlacementState(context);
		if (blockState != null) {
			FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos().up());
			if (fluidState.matches(FluidTags.WATER) && fluidState.method_17811() == 8) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		if (state.getProperty(field_18527) == DoubleBlockHalf.UPPER) {
			BlockState blockState = world.getBlockState(pos.down());
			return blockState.getBlock() == this && blockState.getProperty(field_18527) == DoubleBlockHalf.LOWER;
		} else {
			FluidState fluidState = world.getFluidState(pos);
			return super.canPlaceAt(state, world, pos) && fluidState.matches(FluidTags.WATER) && fluidState.method_17811() == 8;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Fluids.WATER.getStill(false);
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
