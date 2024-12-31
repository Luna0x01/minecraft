package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralFanBlock extends DeadCoralFanBlock {
	private final Block deadCoralBlock;

	protected CoralFanBlock(Block block, Block.Builder builder) {
		super(builder);
		this.deadCoralBlock = block;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		this.checkLivingConditions(state, world, pos);
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!isInWater(state, world, pos)) {
			world.setBlockState(pos, this.deadCoralBlock.getDefaultState().withProperty(WATERLOGGED, Boolean.valueOf(false)), 2);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			this.checkLivingConditions(state, world, pos);
			if ((Boolean)state.getProperty(WATERLOGGED)) {
				world.method_16340().schedule(pos, Fluids.WATER, Fluids.WATER.method_17778(world));
			}

			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		}
	}
}
