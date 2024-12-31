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
	public void method_13648(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		defaultedList.add(new ItemStack(item, 1, 0));
		defaultedList.add(new ItemStack(item, 1, 1));
	}
}
