package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ConcretePowderBlock extends FallingBlock {
	private final BlockState field_18255;

	public ConcretePowderBlock(Block block, Block.Builder builder) {
		super(builder);
		this.field_18255 = block.getDefaultState();
	}

	@Override
	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos) {
		if (method_16654(currentStateInPos)) {
			world.setBlockState(pos, this.field_18255, 3);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockView blockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		return !method_16654(blockView.getBlockState(blockPos)) && !method_16653(blockView, blockPos) ? super.getPlacementState(context) : this.field_18255;
	}

	private static boolean method_16653(BlockView blockView, BlockPos blockPos) {
		boolean bl = false;
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);

		for (Direction direction : Direction.values()) {
			BlockState blockState = blockView.getBlockState(mutable);
			if (direction != Direction.DOWN || method_16654(blockState)) {
				mutable.set(blockPos).move(direction);
				blockState = blockView.getBlockState(mutable);
				if (method_16654(blockState) && !Block.isFaceFullSquare(blockState.getCollisionShape(blockView, blockPos), direction.getOpposite())) {
					bl = true;
					break;
				}
			}
		}

		return bl;
	}

	private static boolean method_16654(BlockState blockState) {
		return blockState.getFluidState().matches(FluidTags.WATER);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return method_16653(world, pos) ? this.field_18255 : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
}
