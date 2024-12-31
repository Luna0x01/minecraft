package net.minecraft.world.gen.layer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

public class FlatWorldLayer {
	private final BlockState blockState;
	private final int thickness;
	private int field_4956;

	public FlatWorldLayer(int i, Block block) {
		this.thickness = i;
		this.blockState = block.getDefaultState();
	}

	public int getThickness() {
		return this.thickness;
	}

	public BlockState getBlockState() {
		return this.blockState;
	}

	public int method_4111() {
		return this.field_4956;
	}

	public void setLayerLevel(int i) {
		this.field_4956 = i;
	}

	public String toString() {
		return (this.thickness > 1 ? this.thickness + "*" : "") + Registry.BLOCK.getId(this.blockState.getBlock());
	}
}
