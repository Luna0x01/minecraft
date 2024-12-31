package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class class_3709 extends Block implements FluidFillable {
	final class_3708 field_18382;

	protected class_3709(class_3708 arg, Block.Builder builder) {
		super(builder);
		this.field_18382 = arg;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Fluids.WATER.getStill(false);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (!state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			if (direction == Direction.UP) {
				Block block = neighborState.getBlock();
				if (block != this && block != this.field_18382) {
					return this.field_18382.method_16689(world);
				}
			}

			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		return block != Blocks.MAGMA_BLOCK && (block == this || Block.isFaceFullSquare(blockState.getCollisionShape(world, blockPos), Direction.UP));
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Blocks.KELP;
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(Blocks.KELP);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		return false;
	}
}
