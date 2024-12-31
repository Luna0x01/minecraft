package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class MushroomBlock extends Block {
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	public static final BooleanProperty UP = ConnectingBlock.UP;
	public static final BooleanProperty DOWN = ConnectingBlock.DOWN;
	private static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY = ConnectingBlock.FACING_TO_PROPERTY;
	@Nullable
	private final Block block;

	public MushroomBlock(@Nullable Block block, Block.Builder builder) {
		super(builder);
		this.block = block;
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(NORTH, Boolean.valueOf(true))
				.withProperty(EAST, Boolean.valueOf(true))
				.withProperty(SOUTH, Boolean.valueOf(true))
				.withProperty(WEST, Boolean.valueOf(true))
				.withProperty(UP, Boolean.valueOf(true))
				.withProperty(DOWN, Boolean.valueOf(true))
		);
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return Math.max(0, random.nextInt(9) - 6);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return (Itemable)(this.block == null ? Items.AIR : this.block);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockView blockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		return this.getDefaultState()
			.withProperty(DOWN, Boolean.valueOf(this != blockView.getBlockState(blockPos.down()).getBlock()))
			.withProperty(UP, Boolean.valueOf(this != blockView.getBlockState(blockPos.up()).getBlock()))
			.withProperty(NORTH, Boolean.valueOf(this != blockView.getBlockState(blockPos.north()).getBlock()))
			.withProperty(EAST, Boolean.valueOf(this != blockView.getBlockState(blockPos.east()).getBlock()))
			.withProperty(SOUTH, Boolean.valueOf(this != blockView.getBlockState(blockPos.south()).getBlock()))
			.withProperty(WEST, Boolean.valueOf(this != blockView.getBlockState(blockPos.west()).getBlock()));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return neighborState.getBlock() == this
			? state.withProperty((Property)FACING_TO_PROPERTY.get(direction), Boolean.valueOf(false))
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty((Property)FACING_TO_PROPERTY.get(rotation.rotate(Direction.NORTH)), state.getProperty(NORTH))
			.withProperty((Property)FACING_TO_PROPERTY.get(rotation.rotate(Direction.SOUTH)), state.getProperty(SOUTH))
			.withProperty((Property)FACING_TO_PROPERTY.get(rotation.rotate(Direction.EAST)), state.getProperty(EAST))
			.withProperty((Property)FACING_TO_PROPERTY.get(rotation.rotate(Direction.WEST)), state.getProperty(WEST))
			.withProperty((Property)FACING_TO_PROPERTY.get(rotation.rotate(Direction.UP)), state.getProperty(UP))
			.withProperty((Property)FACING_TO_PROPERTY.get(rotation.rotate(Direction.DOWN)), state.getProperty(DOWN));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withProperty((Property)FACING_TO_PROPERTY.get(mirror.apply(Direction.NORTH)), state.getProperty(NORTH))
			.withProperty((Property)FACING_TO_PROPERTY.get(mirror.apply(Direction.SOUTH)), state.getProperty(SOUTH))
			.withProperty((Property)FACING_TO_PROPERTY.get(mirror.apply(Direction.EAST)), state.getProperty(EAST))
			.withProperty((Property)FACING_TO_PROPERTY.get(mirror.apply(Direction.WEST)), state.getProperty(WEST))
			.withProperty((Property)FACING_TO_PROPERTY.get(mirror.apply(Direction.UP)), state.getProperty(UP))
			.withProperty((Property)FACING_TO_PROPERTY.get(mirror.apply(Direction.DOWN)), state.getProperty(DOWN));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(UP, DOWN, NORTH, EAST, SOUTH, WEST);
	}
}
