package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4342;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class TorchBlock extends Block {
	protected static final VoxelShape field_18530 = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);

	protected TorchBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18530;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.DOWN && !this.canPlaceAt(state, world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		Block block = blockState.getBlock();
		boolean bl = block instanceof FenceBlock
			|| block instanceof StainedGlassBlock
			|| block == Blocks.GLASS
			|| block == Blocks.COBBLESTONE_WALL
			|| block == Blocks.MOSSY_COBBLESTONE_WALL
			|| blockState.method_16913();
		return bl && block != Blocks.END_GATEWAY;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = (double)pos.getX() + 0.5;
		double e = (double)pos.getY() + 0.7;
		double f = (double)pos.getZ() + 0.5;
		world.method_16343(class_4342.field_21363, d, e, f, 0.0, 0.0, 0.0);
		world.method_16343(class_4342.field_21399, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
