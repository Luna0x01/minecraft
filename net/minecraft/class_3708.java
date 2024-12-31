package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class class_3708 extends Block implements FluidFillable {
	public static final IntProperty field_18380 = Properties.AGE_25;
	protected static final VoxelShape field_18381 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);

	protected class_3708(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18380, Integer.valueOf(0)));
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18381;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		return fluidState.matches(FluidTags.WATER) && fluidState.method_17811() == 8 ? this.method_16689(context.getWorld()) : null;
	}

	public BlockState method_16689(IWorld iWorld) {
		return this.getDefaultState().withProperty(field_18380, Integer.valueOf(iWorld.getRandom().nextInt(25)));
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
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.method_8535(pos, true);
		} else {
			BlockPos blockPos = pos.up();
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.WATER && (Integer)state.getProperty(field_18380) < 25 && random.nextDouble() < 0.14) {
				world.setBlockState(blockPos, state.method_16930(field_18380));
			}
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		return block == Blocks.MAGMA_BLOCK
			? false
			: block == this || block == Blocks.KELP_PLANT || Block.isFaceFullSquare(blockState.getCollisionShape(world, blockPos), Direction.UP);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (!state.canPlaceAt(world, pos)) {
			if (direction == Direction.DOWN) {
				return Blocks.AIR.getDefaultState();
			}

			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		if (direction == Direction.UP && neighborState.getBlock() == this) {
			return Blocks.KELP_PLANT.getDefaultState();
		} else {
			world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18380);
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
