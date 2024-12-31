package net.minecraft.block;

public abstract class GourdBlock extends Block {
	public GourdBlock(Block.Builder builder) {
		super(builder);
	}

	public abstract StemBlock getStem();

	public abstract AttachedStemBlock getAttachedStem();
}
