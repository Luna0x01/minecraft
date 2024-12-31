package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public class class_2960 {
	@Nullable
	public static ItemStack method_12933(ItemStack[] itemStacks, int i, int j) {
		if (i >= 0 && i < itemStacks.length && itemStacks[i] != null && j > 0) {
			ItemStack itemStack = itemStacks[i].split(j);
			if (itemStacks[i].count == 0) {
				itemStacks[i] = null;
			}

			return itemStack;
		} else {
			return null;
		}
	}

	@Nullable
	public static ItemStack method_12932(ItemStack[] itemStacks, int i) {
		if (i >= 0 && i < itemStacks.length) {
			ItemStack itemStack = itemStacks[i];
			itemStacks[i] = null;
			return itemStack;
		} else {
			return null;
		}
	}
}
