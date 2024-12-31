package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class DoubleStoneSlabBlock extends StoneSlabBlock {
	@Override
	public boolean isDoubleSlab() {
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.SOLID;
	}
}
