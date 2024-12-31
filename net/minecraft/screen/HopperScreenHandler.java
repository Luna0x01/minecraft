package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;

public class HopperScreenHandler extends ScreenHandler {
	private final Inventory inventory;

	public HopperScreenHandler(PlayerInventory playerInventory, Inventory inventory, PlayerEntity playerEntity) {
		this.inventory = inventory;
		inventory.onInvOpen(playerEntity);
		int i = 51;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			this.addSlot(new Slot(inventory, j, 44 + j * 18, 20));
		}

		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 9; l++) {
				this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, k * 18 + i));
			}
		}

		for (int m = 0; m < 9; m++) {
			this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 58 + i));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUseInv(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = null;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot < this.inventory.getInvSize()) {
				if (!this.insertItem(itemStack2, this.inventory.getInvSize(), this.slots.size(), true)) {
					return null;
				}
			} else if (!this.insertItem(itemStack2, 0, this.inventory.getInvSize(), false)) {
				return null;
			}

			if (itemStack2.count == 0) {
				slot.setStack(null);
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
