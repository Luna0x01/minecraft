package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4342;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndRodBlock extends FacingBlock {
	protected static final VoxelShape field_18310 = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
	protected static final VoxelShape field_18311 = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
	protected static final VoxelShape field_18312 = Block.createCuboidShape(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);

	protected EndRodBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.UP));
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getProperty(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withProperty(FACING, mirror.apply(state.getProperty(FACING)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		switch (((Direction)state.getProperty(FACING)).getAxis()) {
			case X:
			default:
				return field_18312;
			case Z:
				return field_18311;
			case Y:
				return field_18310;
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction direction = context.method_16151();
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos().offset(direction.getOpposite()));
		return blockState.getBlock() == this && blockState.getProperty(FACING) == direction
			? this.getDefaultState().withProperty(FACING, direction.getOpposite())
			: this.getDefaultState().withProperty(FACING, direction);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		Direction direction = state.getProperty(FACING);
		double d = (double)pos.getX() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double e = (double)pos.getY() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double f = (double)pos.getZ() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double g = (double)(0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F);
		if (random.nextInt(5) == 0) {
			world.method_16343(
				class_4342.field_21392,
				d + (double)direction.getOffsetX() * g,
				e + (double)direction.getOffsetY() * g,
				f + (double)direction.getOffsetZ() * g,
				random.nextGaussian() * 0.005,
				random.nextGaussian() * 0.005,
				random.nextGaussian() * 0.005
			);
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.NORMAL;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
