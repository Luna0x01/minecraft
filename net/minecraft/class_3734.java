package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;

public class class_3734 extends class_3685 {
	public static final DirectionProperty field_18580 = HorizontalFacingBlock.FACING;
	private static final Map<Direction, VoxelShape> field_18581 = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			Block.createCuboidShape(4.0, 4.0, 8.0, 12.0, 12.0, 16.0),
			Direction.SOUTH,
			Block.createCuboidShape(4.0, 4.0, 0.0, 12.0, 12.0, 8.0),
			Direction.EAST,
			Block.createCuboidShape(0.0, 4.0, 4.0, 8.0, 12.0, 12.0),
			Direction.WEST,
			Block.createCuboidShape(8.0, 4.0, 4.0, 16.0, 12.0, 12.0)
		)
	);

	protected class_3734(SkullBlock.class_3722 arg, Block.Builder builder) {
		super(arg, builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18580, Direction.NORTH));
	}

	@Override
	public String getTranslationKey() {
		return this.getItem().getTranslationKey();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return (VoxelShape)field_18581.get(state.getProperty(field_18580));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = this.getDefaultState();
		BlockView blockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		Direction[] directions = context.method_16021();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.withProperty(field_18580, direction2);
				if (!blockView.getBlockState(blockPos.offset(direction)).canReplace(context)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(field_18580, rotation.rotate(state.getProperty(field_18580)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(field_18580)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18580);
	}
}
