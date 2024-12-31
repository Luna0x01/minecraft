package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BeaconScreenHandler extends ScreenHandler {
	private Inventory paymentInventory;
	private final BeaconScreenHandler.PaymentSlot paymentSlot;

	public BeaconScreenHandler(Inventory inventory, Inventory inventory2) {
		this.paymentInventory = inventory2;
		this.addSlot(this.paymentSlot = new BeaconScreenHandler.PaymentSlot(inventory2, 0, 136, 110));
		int i = 36;
		int j = 137;

		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 9; l++) {
				this.addSlot(new Slot(inventory, l + k * 9 + 9, i + l * 18, j + k * 18));
			}
		}

		for (int m = 0; m < 9; m++) {
			this.addSlot(new Slot(inventory, m, i + m * 18, 58 + j));
		}
	}

	@Override
	public void addListener(ScreenHandlerListener listener) {
		super.addListener(listener);
		listener.onScreenHandlerInventoryUpdate(this, this.paymentInventory);
	}

	@Override
	public void setProperty(int id, int value) {
		this.paymentInventory.setProperty(id, value);
	}

	public Inventory getPaymentInventory() {
		return this.paymentInventory;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		if (player != null && !player.world.isClient) {
			ItemStack itemStack = this.paymentSlot.takeStack(this.paymentSlot.getMaxStackAmount());
			if (itemStack != null) {
				player.dropItem(itemStack, false);
			}
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.paymentInventory.canPlayerUseInv(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = null;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot == 0) {
				if (!this.insertItem(itemStack2, 1, 37, true)) {
					return null;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (!this.paymentSlot.hasStack() && this.paymentSlot.canInsert(itemStack2) && itemStack2.count == 1) {
				if (!this.insertItem(itemStack2, 0, 1, false)) {
					return null;
				}
			} else if (invSlot >= 1 && invSlot < 28) {
				if (!this.insertItem(itemStack2, 28, 37, false)) {
					return null;
				}
			} else if (invSlot >= 28 && invSlot < 37) {
				if (!this.insertItem(itemStack2, 1, 28, false)) {
					return null;
				}
			} else if (!this.insertItem(itemStack2, 1, 37, false)) {
				return null;
			}

			if (itemStack2.count == 0) {
				slot.setStack(null);
			} else {
				slot.markDirty();
			}

			if (itemStack2.count == itemStack.count) {
				return null;
			}

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	class PaymentSlot extends Slot {
		public PaymentSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return stack == null
				? false
				: stack.getItem() == Items.EMERALD || stack.getItem() == Items.DIAMOND || stack.getItem() == Items.GOLD_INGOT || stack.getItem() == Items.IRON_INGOT;
		}

		@Override
		public int getMaxStackAmount() {
			return 1;
		}
	}
}
