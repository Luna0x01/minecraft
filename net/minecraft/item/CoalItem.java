package net.minecraft.item;

import java.util.List;
import net.minecraft.item.itemgroup.ItemGroup;

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
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}
}
