package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;

public class GlazedTerracottaBlock extends HorizontalFacingBlock {
	public GlazedTerracottaBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(FACING, context.method_16145().getOpposite());
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.PUSH_ONLY;
	}
}
