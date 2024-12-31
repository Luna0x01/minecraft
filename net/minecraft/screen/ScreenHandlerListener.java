package net.minecraft.screen;

import java.util.List;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface ScreenHandlerListener {
	void updateScreenHandler(ScreenHandler handler, List<ItemStack> list);

	void onScreenHandlerSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack);

	void onScreenHandlerPropertyUpdate(ScreenHandler handler, int propertyId, int value);

	void onScreenHandlerInventoryUpdate(ScreenHandler handler, Inventory inventory);
}
