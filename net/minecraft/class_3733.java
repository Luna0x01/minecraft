package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;

public class class_3733 extends AbstractBannerBlock {
	public static final DirectionProperty field_18574 = HorizontalFacingBlock.FACING;
	private static final Map<Direction, VoxelShape> field_18575 = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			Block.createCuboidShape(0.0, 0.0, 14.0, 16.0, 12.5, 16.0),
			Direction.SOUTH,
			Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.5, 2.0),
			Direction.WEST,
			Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 12.5, 16.0),
			Direction.EAST,
			Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 12.5, 16.0)
		)
	);

	public class_3733(DyeColor dyeColor, Block.Builder builder) {
		super(dyeColor, builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18574, Direction.NORTH));
	}

	@Override
	public String getTranslationKey() {
		return this.getItem().getTranslationKey();
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		return world.getBlockState(pos.offset(((Direction)state.getProperty(field_18574)).getOpposite())).getMaterial().isSolid();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction == ((Direction)state.getProperty(field_18574)).getOpposite() && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return (VoxelShape)field_18575.get(state.getProperty(field_18574));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = this.getDefaultState();
		RenderBlockView renderBlockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		Direction[] directions = context.method_16021();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.withProperty(field_18574, direction2);
				if (blockState.canPlaceAt(renderBlockView, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(field_18574, rotation.rotate(state.getProperty(field_18574)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(field_18574)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18574);
	}
}
