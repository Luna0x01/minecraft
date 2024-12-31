package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;

public class CreativeInventoryListener implements ScreenHandlerListener {
	private final MinecraftClient client;

	public CreativeInventoryListener(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void updateScreenHandler(ScreenHandler handler, List<ItemStack> list) {
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
