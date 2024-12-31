package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class AttachedStemBlock extends PlantBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private final GourdBlock gourdBlock;
	private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.SOUTH,
			Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 16.0),
			Direction.WEST,
			Block.createCuboidShape(0.0, 0.0, 6.0, 10.0, 10.0, 10.0),
			Direction.NORTH,
			Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 10.0, 10.0),
			Direction.EAST,
			Block.createCuboidShape(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)
		)
	);

	protected AttachedStemBlock(GourdBlock gourdBlock, Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.NORTH));
		this.gourdBlock = gourdBlock;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return (VoxelShape)FACING_TO_SHAPE.get(state.getProperty(FACING));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return neighborState.getBlock() != this.gourdBlock && direction == state.getProperty(FACING)
			? this.gourdBlock.getStem().getDefaultState().withProperty(StemBlock.AGE, Integer.valueOf(7))
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
		return state.getBlock() == Blocks.FARMLAND;
	}

	protected Item getPickBlockItem() {
		if (this.gourdBlock == Blocks.PUMPKIN) {
			return Items.PUMPKIN_SEEDS;
		} else {
			return this.gourdBlock == Blocks.MELON_BLOCK ? Items.MELON_SEEDS : Items.AIR;
		}
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(this.getPickBlockItem());
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
		builder.method_16928(FACING);
	}
}
