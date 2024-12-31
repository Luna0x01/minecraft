package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class GrassPathBlock extends Block {
	protected static final VoxelShape SHAPE = FarmlandBlock.SHAPE;

	protected GrassPathBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasSidedTransparency(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return !this.getDefaultState().canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())
			? Block.pushEntitiesUpBeforeBlockChange(
				this.getDefaultState(), Blocks.field_10566.getDefaultState(), itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos()
			)
			: super.getPlacementState(itemPlacementContext);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction == Direction.field_11036 && !blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		FarmlandBlock.setToDirt(blockState, serverWorld, blockPos);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		BlockState blockState2 = worldView.getBlockState(blockPos.up());
		return !blockState2.getMaterial().isSolid() || blockState2.getBlock() instanceof FenceGateBlock;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return SHAPE;
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	@Override
	public boolean hasInWallOverlay(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}
}
