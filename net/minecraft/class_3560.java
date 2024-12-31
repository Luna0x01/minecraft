package net.minecraft;

import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class class_3560 extends BlockItem {
	public class_3560(Item.Settings settings) {
		super(Blocks.TRIPWIRE, settings);
	}

	@Override
	public String getTranslationKey() {
		return this.computeTranslationKey();
	}
}
