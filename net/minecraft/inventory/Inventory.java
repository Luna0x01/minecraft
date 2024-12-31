package net.minecraft.inventory;

import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;

public interface Inventory extends Clearable {
	int size();

	boolean isEmpty();

	ItemStack getStack(int slot);

	ItemStack removeStack(int slot, int amount);

	ItemStack removeStack(int slot);

	void setStack(int slot, ItemStack stack);

	default int getMaxCountPerStack() {
		return 64;
	}

	void markDirty();

	boolean canPlayerUse(PlayerEntity player);

	default void onOpen(PlayerEntity player) {
	}

	default void onClose(PlayerEntity player) {
	}

	default boolean isValid(int slot, ItemStack stack) {
		return true;
	}

	default int count(Item item) {
		int i = 0;

		for (int j = 0; j < this.size(); j++) {
			ItemStack itemStack = this.getStack(j);
			if (itemStack.getItem().equals(item)) {
				i += itemStack.getCount();
			}
		}

		return i;
	}

	default boolean containsAny(Set<Item> items) {
		for (int i = 0; i < this.size(); i++) {
			ItemStack itemStack = this.getStack(i);
			if (items.contains(itemStack.getItem()) && itemStack.getCount() > 0) {
				return true;
			}
		}

		return false;
	}
}
