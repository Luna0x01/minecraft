package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;

public class DeadCoralFanBlock extends CoralParentBlock {
	private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

	protected DeadCoralFanBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}
}
