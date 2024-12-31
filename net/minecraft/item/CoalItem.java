package net.minecraft.item;

import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.collection.DefaultedList;

public class CoalItem extends Item {
	public CoalItem() {
		this.setUnbreakable(true);
		this.setMaxDamage(0);
		this.setItemGroup(ItemGroup.MATERIALS);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return stack.getData() == 1 ? "item.charcoal" : "item.coal";
	}

	@Override
	public void appendToItemGroup(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.canAddTo(group)) {
			stacks.add(new ItemStack(this, 1, 0));
			stacks.add(new ItemStack(this, 1, 1));
		}
	}
}
