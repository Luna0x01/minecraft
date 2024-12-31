package net.minecraft.item;

import net.minecraft.block.Block;

public class StickyPistonBlockItem extends BlockItem {
	public StickyPistonBlockItem(Block block) {
		super(block);
	}

	@Override
	public int getMeta(int i) {
		return 7;
	}
}
