package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Nameable;

public interface Inventory extends Nameable {
	int getInvSize();

	boolean isEmpty();

	ItemStack getInvStack(int slot);

	ItemStack takeInvStack(int slot, int amount);

	ItemStack removeInvStack(int slot);

	void setInvStack(int slot, ItemStack stack);

	int getInvMaxStackAmount();

	void markDirty();

	boolean canPlayerUseInv(PlayerEntity player);

	void onInvOpen(PlayerEntity player);

	void onInvClose(PlayerEntity player);

	boolean isValidInvStack(int slot, ItemStack stack);

	int getProperty(int key);

	void setProperty(int id, int value);

	int getProperties();

	void clear();
}
