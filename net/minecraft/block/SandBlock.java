package net.minecraft.block;

public class SandBlock extends FallingBlock {
	private final int field_18461;

	public SandBlock(int i, Block.Builder builder) {
		super(builder);
		this.field_18461 = i;
	}

	@Override
	public int getColor(BlockState state) {
		return this.field_18461;
	}
}
