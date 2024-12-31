package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StainedGlassBlock extends TransparentBlock {
	private final DyeColor field_18499;

	public StainedGlassBlock(DyeColor dyeColor, Block.Builder builder) {
		super(builder);
		this.field_18499 = dyeColor;
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	public DyeColor method_16739() {
		return this.field_18499;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (oldState.getBlock() != state.getBlock()) {
			if (!world.isClient) {
				BeaconBlock.updateState(world, pos);
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			if (!world.isClient) {
				BeaconBlock.updateState(world, pos);
			}
		}
	}
}
