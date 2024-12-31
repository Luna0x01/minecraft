package net.minecraft.item;

import net.minecraft.block.Block;

public class ShulkerBoxItem extends BlockItem {
	public ShulkerBoxItem(Block block) {
		super(block);
		this.setMaxCount(1);
	}
}
