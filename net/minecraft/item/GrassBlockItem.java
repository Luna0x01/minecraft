package net.minecraft.item;

import net.minecraft.block.Block;

public class GrassBlockItem extends BlockItem {
	private final Block coloredBlock;
	private String[] names;

	public GrassBlockItem(Block block, boolean bl) {
		super(block);
		this.coloredBlock = block;
		if (bl) {
			this.setMaxDamage(0);
			this.setUnbreakable(true);
		}
	}

	@Override
	public int getDisplayColor(ItemStack stack, int color) {
		return this.coloredBlock.getColor(this.coloredBlock.stateFromData(stack.getData()));
	}

	@Override
	public int getMeta(int i) {
		return i;
	}

	public GrassBlockItem setNamed(String[] names) {
		this.names = names;
		return this;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (this.names == null) {
			return super.getTranslationKey(stack);
		} else {
			int i = stack.getData();
			return i >= 0 && i < this.names.length ? super.getTranslationKey(stack) + "." + this.names[i] : super.getTranslationKey(stack);
		}
	}
}
