package net.minecraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public interface SidedInventory extends Inventory {
	int[] getAvailableSlots(Direction side);

	boolean canInsertInvStack(int slot, ItemStack stack, Direction dir);

	boolean canExtractInvStack(int slot, ItemStack stack, Direction dir);
}
