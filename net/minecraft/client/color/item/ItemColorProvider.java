package net.minecraft.client.color.item;

import net.minecraft.item.ItemStack;

public interface ItemColorProvider {
	int getColor(ItemStack itemStack, int i);
}
