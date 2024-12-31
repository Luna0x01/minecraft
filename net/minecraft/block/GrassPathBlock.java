package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Itemable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class GrassPathBlock extends Block {
	protected static final VoxelShape field_18351 = FarmlandBlock.field_18318;

	protected GrassPathBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public int getLightSubtracted(BlockState state, BlockView world, BlockPos pos) {
		return world.getMaxLightLevel();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return !this.getDefaultState().canPlaceAt(context.getWorld(), context.getBlockPos())
			? Block.pushEntitiesUpBeforeBlockChange(this.getDefaultState(), Blocks.DIRT.getDefaultState(), context.getWorld(), context.getBlockPos())
			: super.getPlacementState(context);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.UP && !state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		FarmlandBlock.method_16675(state, world, pos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.up());
		return !blockState.getMaterial().isSolid() || blockState.getBlock() instanceof FenceGateBlock;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18351;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Blocks.DIRT;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
