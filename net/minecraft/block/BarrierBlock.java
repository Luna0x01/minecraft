package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BarrierBlock extends Block {
	protected BarrierBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state) {
		return 1.0F;
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
	}
}
