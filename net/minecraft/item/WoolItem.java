package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;

public class WoolItem extends BlockItem {
	public WoolItem(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setUnbreakable(true);
	}

	@Override
	public int getMeta(int i) {
		return i;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey() + "." + DyeColor.byId(stack.getData()).getTranslationKey();
	}
}
