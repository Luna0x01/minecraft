package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Nameable;

public interface Inventory extends Nameable {
	int getInvSize();

	@Nullable
	ItemStack getInvStack(int slot);

	@Nullable
	ItemStack takeInvStack(int slot, int amount);

	@Nullable
	ItemStack removeInvStack(int slot);

	void setInvStack(int slot, @Nullable ItemStack stack);

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
