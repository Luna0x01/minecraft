package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;

public class WallSignBlock extends AbstractSignBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private static final Map<Direction, VoxelShape> field_18579 = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			Block.createCuboidShape(0.0, 4.5, 14.0, 16.0, 12.5, 16.0),
			Direction.SOUTH,
			Block.createCuboidShape(0.0, 4.5, 0.0, 16.0, 12.5, 2.0),
			Direction.EAST,
			Block.createCuboidShape(0.0, 4.5, 0.0, 2.0, 12.5, 16.0),
			Direction.WEST,
			Block.createCuboidShape(14.0, 4.5, 0.0, 16.0, 12.5, 16.0)
		)
	);

	public WallSignBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.NORTH).withProperty(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public String getTranslationKey() {
		return this.getItem().getTranslationKey();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return (VoxelShape)field_18579.get(state.getProperty(FACING));
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		return world.getBlockState(pos.offset(((Direction)state.getProperty(FACING)).getOpposite())).getMaterial().isSolid();
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = this.getDefaultState();
		FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		RenderBlockView renderBlockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		Direction[] directions = context.method_16021();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.withProperty(FACING, direction2);
				if (blockState.canPlaceAt(renderBlockView, blockPos)) {
					return blockState.withProperty(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
				}
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction.getOpposite() == state.getProperty(FACING) && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getProperty(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, WATERLOGGED);
	}
}
