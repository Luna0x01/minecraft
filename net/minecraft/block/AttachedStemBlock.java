package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class AttachedStemBlock extends PlantBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private final GourdBlock gourdBlock;
	private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.field_11035,
			Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 16.0),
			Direction.field_11039,
			Block.createCuboidShape(0.0, 0.0, 6.0, 10.0, 10.0, 10.0),
			Direction.field_11043,
			Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 10.0, 10.0),
			Direction.field_11034,
			Block.createCuboidShape(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)
		)
	);

	protected AttachedStemBlock(GourdBlock gourdBlock, Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.field_11043));
		this.gourdBlock = gourdBlock;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return (VoxelShape)FACING_TO_SHAPE.get(blockState.get(FACING));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return blockState2.getBlock() != this.gourdBlock && direction == blockState.get(FACING)
			? this.gourdBlock.getStem().getDefaultState().with(StemBlock.AGE, Integer.valueOf(7))
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getBlock() == Blocks.field_10362;
	}

	protected Item getSeeds() {
		if (this.gourdBlock == Blocks.field_10261) {
			return Items.field_8706;
		} else {
			return this.gourdBlock == Blocks.field_10545 ? Items.field_8188 : Items.AIR;
		}
	}

	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this.getSeeds());
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
