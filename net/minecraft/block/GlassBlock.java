package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class GlassBlock extends TransparentBlock {
	public GlassBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}
}
