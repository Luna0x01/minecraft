package net.minecraft.block;

import net.minecraft.class_3706;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StainedGlassPaneBlock extends class_3706 {
	private final DyeColor field_18500;

	public StainedGlassPaneBlock(DyeColor dyeColor, Block.Builder builder) {
		super(builder);
		this.field_18500 = dyeColor;
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(field_18265, Boolean.valueOf(false))
				.withProperty(field_18266, Boolean.valueOf(false))
				.withProperty(field_18267, Boolean.valueOf(false))
				.withProperty(field_18268, Boolean.valueOf(false))
				.withProperty(field_18269, Boolean.valueOf(false))
		);
	}

	public DyeColor method_16740() {
		return this.field_18500;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
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
