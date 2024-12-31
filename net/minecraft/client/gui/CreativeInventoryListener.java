package net.minecraft.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.collection.DefaultedList;

public class CreativeInventoryListener implements ScreenHandlerListener {
	private final MinecraftClient client;

	public CreativeInventoryListener(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void method_13643(ScreenHandler screenHandler, DefaultedList<ItemStack> defaultedList) {
	}

	@Override
	public void onScreenHandlerSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
		this.client.interactionManager.clickCreativeStack(stack, slotId);
	}

	@Override
	public void onScreenHandlerPropertyUpdate(ScreenHandler handler, int propertyId, int value) {
	}

	@Override
	public void onScreenHandlerInventoryUpdate(ScreenHandler handler, Inventory inventory) {
	}
}
