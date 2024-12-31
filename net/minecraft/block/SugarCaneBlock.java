package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.FluidState;
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

public class SugarCaneBlock extends Block {
	public static final IntProperty field_18524 = Properties.AGE_15;
	protected static final VoxelShape field_18525 = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

	protected SugarCaneBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18524, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18525;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.canPlaceAt(world, pos) && world.method_8579(pos.up())) {
			int i = 1;

			while (world.getBlockState(pos.down(i)).getBlock() == this) {
				i++;
			}

			if (i < 3) {
				int j = (Integer)state.getProperty(field_18524);
				if (j == 15) {
					world.setBlockState(pos.up(), this.getDefaultState());
					world.setBlockState(pos, state.withProperty(field_18524, Integer.valueOf(0)), 4);
				} else {
					world.setBlockState(pos, state.withProperty(field_18524, Integer.valueOf(j + 1)), 4);
				}
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		Block block = world.getBlockState(pos.down()).getBlock();
		if (block == this) {
			return true;
		} else {
			if (block == Blocks.GRASS_BLOCK
				|| block == Blocks.DIRT
				|| block == Blocks.COARSE_DIRT
				|| block == Blocks.PODZOL
				|| block == Blocks.SAND
				|| block == Blocks.RED_SAND) {
				BlockPos blockPos = pos.down();

				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					BlockState blockState = world.getBlockState(blockPos.offset(direction));
					FluidState fluidState = world.getFluidState(blockPos.offset(direction));
					if (fluidState.matches(FluidTags.WATER) || blockState.getBlock() == Blocks.FROSTED_ICE) {
						return true;
					}
				}
			}

			return false;
		}
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
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18524);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
