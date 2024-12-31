package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Itemable;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class class_3725 extends Block {
	public static final BooleanProperty field_18495 = Properties.SNOWY;

	protected class_3725(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18495, Boolean.valueOf(false)));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (direction != Direction.UP) {
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			Block block = neighborState.getBlock();
			return state.withProperty(field_18495, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW));
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Block block = context.getWorld().getBlockState(context.getBlockPos().up()).getBlock();
		return this.getDefaultState().withProperty(field_18495, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18495);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Blocks.DIRT;
	}
}
