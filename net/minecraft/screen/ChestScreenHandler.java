package net.minecraft.screen;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;

public class ChestScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final int height;

	public ChestScreenHandler(Inventory inventory, Inventory inventory2, PlayerEntity playerEntity) {
		this.inventory = inventory2;
		this.height = inventory2.getInvSize() / 9;
		inventory2.onInvOpen(playerEntity);
		int i = (this.height - 4) * 18;

		for (int j = 0; j < this.height; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(inventory2, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.addSlot(new Slot(inventory, m + l * 9 + 9, 8 + m * 18, 103 + l * 18 + i));
			}
		}

		for (int n = 0; n < 9; n++) {
			this.addSlot(new Slot(inventory, n, 8 + n * 18, 161 + i));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUseInv(player);
	}

	@Nullable
	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = null;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot < this.height * 9) {
				if (!this.insertItem(itemStack2, this.height * 9, this.slots.size(), true)) {
					return null;
				}
			} else if (!this.insertItem(itemStack2, 0, this.height * 9, false)) {
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

	public Inventory getInventory() {
		return this.inventory;
	}
}
