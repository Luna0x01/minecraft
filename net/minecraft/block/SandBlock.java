package net.minecraft.block;

public class SandBlock extends FallingBlock {
	private final int color;

	public SandBlock(int i, Block.Settings settings) {
		super(settings);
		this.color = i;
	}

	@Override
	public int getColor(BlockState blockState) {
		return this.color;
	}
}
