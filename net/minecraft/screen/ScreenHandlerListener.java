package net.minecraft.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface ScreenHandlerListener {
	void method_13643(ScreenHandler screenHandler, DefaultedList<ItemStack> defaultedList);

	void onScreenHandlerSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack);

	void onScreenHandlerPropertyUpdate(ScreenHandler handler, int propertyId, int value);

	void onScreenHandlerInventoryUpdate(ScreenHandler handler, Inventory inventory);
}
