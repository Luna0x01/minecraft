package net.minecraft.item;

import net.minecraft.block.LeavesBlock;

public class LeavesItem extends BlockItem {
	private final LeavesBlock leavesBlock;

	public LeavesItem(LeavesBlock leavesBlock) {
		super(leavesBlock);
		this.leavesBlock = leavesBlock;
		this.setMaxDamage(0);
		this.setUnbreakable(true);
	}

	@Override
	public int getMeta(int i) {
		return i | 4;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey() + "." + this.leavesBlock.getWoodType(stack.getData()).getOldName();
	}
}
