package net.minecraft.block;

public class GravelBlock extends FallingBlock {
	public GravelBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public int getColor(BlockState blockState) {
		return -8356741;
	}
}
