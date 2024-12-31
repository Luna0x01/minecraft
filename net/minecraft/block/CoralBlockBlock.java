package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Itemable;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralBlockBlock extends Block {
	private final Block deadCoralBlock;

	public CoralBlockBlock(Block block, Block.Builder builder) {
		super(builder);
		this.deadCoralBlock = block;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!this.isInWater(world, pos)) {
			world.setBlockState(pos, this.deadCoralBlock.getDefaultState(), 2);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (!this.isInWater(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 60 + world.getRandom().nextInt(40));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	protected boolean isInWater(BlockView world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			FluidState fluidState = world.getFluidState(pos.offset(direction));
			if (fluidState.matches(FluidTags.WATER)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		if (!this.isInWater(context.getWorld(), context.getBlockPos())) {
			context.getWorld().getBlockTickScheduler().schedule(context.getBlockPos(), this, 60 + context.getWorld().getRandom().nextInt(40));
		}

		return this.getDefaultState();
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return this.deadCoralBlock;
	}
}
