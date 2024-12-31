package net.minecraft.screen;

import net.minecraft.class_3055;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;

public class ShulkerBoxScreenHandler extends ScreenHandler {
	private final Inventory inventory;

	public ShulkerBoxScreenHandler(PlayerInventory playerInventory, Inventory inventory, PlayerEntity playerEntity) {
		this.inventory = inventory;
		inventory.onInvOpen(playerEntity);
		int i = 3;
		int j = 9;

		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 9; l++) {
				this.addSlot(new class_3055(inventory, l + k * 9, 8 + l * 18, 18 + k * 18));
			}
		}

		for (int m = 0; m < 3; m++) {
			for (int n = 0; n < 9; n++) {
				this.addSlot(new Slot(playerInventory, n + m * 9 + 9, 8 + n * 18, 84 + m * 18));
			}
		}

		for (int o = 0; o < 9; o++) {
			this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUseInv(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot < this.inventory.getInvSize()) {
				if (!this.insertItem(itemStack2, this.inventory.getInvSize(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 0, this.inventory.getInvSize(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.inventory.onInvClose(player);
	}
}
