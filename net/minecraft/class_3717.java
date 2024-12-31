package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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

public class class_3717 extends RedstoneTorchBlock {
	public static final DirectionProperty field_18455 = HorizontalFacingBlock.FACING;
	public static final BooleanProperty field_18456 = RedstoneTorchBlock.field_18451;

	protected class_3717(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18455, Direction.NORTH).withProperty(field_18456, Boolean.valueOf(true)));
	}

	@Override
	public String getTranslationKey() {
		return this.getItem().getTranslationKey();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return Blocks.WALL_TORCH.getOutlineShape(state, world, pos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		return Blocks.WALL_TORCH.canPlaceAt(state, world, pos);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return Blocks.WALL_TORCH.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = Blocks.WALL_TORCH.getPlacementState(context);
		return blockState == null ? null : this.getDefaultState().withProperty(field_18455, blockState.getProperty(field_18455));
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.getProperty(field_18456)) {
			Direction direction = ((Direction)state.getProperty(field_18455)).getOpposite();
			double d = 0.27;
			double e = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetX();
			double f = (double)pos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2 + 0.22;
			double g = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetZ();
			world.method_16343(class_4338.field_21339, e, f, g, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected boolean shouldNotBeLit(World world, BlockPos pos, BlockState state) {
		Direction direction = ((Direction)state.getProperty(field_18455)).getOpposite();
		return world.isEmittingRedstonePower(pos.offset(direction), direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getProperty(field_18456) && state.getProperty(field_18455) != direction ? 15 : 0;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return Blocks.WALL_TORCH.withRotation(state, rotation);
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return Blocks.WALL_TORCH.withMirror(state, mirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18455, field_18456);
	}
}
