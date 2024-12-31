package net.minecraft.item;

import net.minecraft.block.Block;

public class AnvilItem extends VariantBlockItem {
	public AnvilItem(Block block) {
		super(block, block, new String[]{"intact", "slightlyDamaged", "veryDamaged"});
	}

	@Override
	public int getMeta(int i) {
		return i << 2;
	}
}
