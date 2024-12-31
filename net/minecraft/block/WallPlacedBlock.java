package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;

public class WallPlacedBlock extends HorizontalFacingBlock {
	public static final EnumProperty<WallMountLocation> FACE = Properties.WALL_MOUNT_LOCATION;

	protected WallPlacedBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		Direction direction = getDirection(state).getOpposite();
		BlockPos blockPos = pos.offset(direction);
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (method_14308(block)) {
			return false;
		} else {
			boolean bl = blockState.getRenderLayer(world, blockPos, direction.getOpposite()) == BlockRenderLayer.SOLID;
			return direction == Direction.UP ? block == Blocks.HOPPER || bl : !method_14309(block) && bl;
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		for (Direction direction : context.method_16021()) {
			BlockState blockState;
			if (direction.getAxis() == Direction.Axis.Y) {
				blockState = this.getDefaultState()
					.withProperty(FACE, direction == Direction.UP ? WallMountLocation.CEILING : WallMountLocation.FLOOR)
					.withProperty(FACING, context.method_16145());
			} else {
				blockState = this.getDefaultState().withProperty(FACE, WallMountLocation.WALL).withProperty(FACING, direction.getOpposite());
			}

			if (blockState.canPlaceAt(context.getWorld(), context.getBlockPos())) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return getDirection(state).getOpposite() == direction && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	protected static Direction getDirection(BlockState state) {
		switch ((WallMountLocation)state.getProperty(FACE)) {
			case CEILING:
				return Direction.DOWN;
			case FLOOR:
				return Direction.UP;
			default:
				return state.getProperty(FACING);
		}
	}
}
