package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.TorchBlock;
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
import net.minecraft.world.World;

public class class_3735 extends TorchBlock {
	public static final DirectionProperty field_18582 = HorizontalFacingBlock.FACING;
	private static final Map<Direction, VoxelShape> field_18583 = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			Block.createCuboidShape(5.5, 3.0, 11.0, 10.5, 13.0, 16.0),
			Direction.SOUTH,
			Block.createCuboidShape(5.5, 3.0, 0.0, 10.5, 13.0, 5.0),
			Direction.WEST,
			Block.createCuboidShape(11.0, 3.0, 5.5, 16.0, 13.0, 10.5),
			Direction.EAST,
			Block.createCuboidShape(0.0, 3.0, 5.5, 5.0, 13.0, 10.5)
		)
	);

	protected class_3735(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18582, Direction.NORTH));
	}

	@Override
	public String getTranslationKey() {
		return this.getItem().getTranslationKey();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return (VoxelShape)field_18583.get(state.getProperty(field_18582));
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		Direction direction = state.getProperty(field_18582);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.getRenderLayer(world, blockPos, direction) == BlockRenderLayer.SOLID && !method_14309(blockState.getBlock());
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = this.getDefaultState();
		RenderBlockView renderBlockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		Direction[] directions = context.method_16021();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.withProperty(field_18582, direction2);
				if (blockState.canPlaceAt(renderBlockView, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction.getOpposite() == state.getProperty(field_18582) && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : state;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		Direction direction = state.getProperty(field_18582);
		double d = (double)pos.getX() + 0.5;
		double e = (double)pos.getY() + 0.7;
		double f = (double)pos.getZ() + 0.5;
		double g = 0.22;
		double h = 0.27;
		Direction direction2 = direction.getOpposite();
		world.method_16343(class_4342.field_21363, d + 0.27 * (double)direction2.getOffsetX(), e + 0.22, f + 0.27 * (double)direction2.getOffsetZ(), 0.0, 0.0, 0.0);
		world.method_16343(class_4342.field_21399, d + 0.27 * (double)direction2.getOffsetX(), e + 0.22, f + 0.27 * (double)direction2.getOffsetZ(), 0.0, 0.0, 0.0);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(field_18582, rotation.rotate(state.getProperty(field_18582)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(field_18582)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18582);
	}
}
