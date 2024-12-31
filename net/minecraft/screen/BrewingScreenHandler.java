package net.minecraft.screen;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BrewingScreenHandler extends ScreenHandler {
	private Inventory inventory;
	private final Slot ingredientSlot;
	private int field_4094;

	public BrewingScreenHandler(PlayerInventory playerInventory, Inventory inventory) {
		this.inventory = inventory;
		this.addSlot(new BrewingScreenHandler.PotionSlot(playerInventory.player, inventory, 0, 56, 46));
		this.addSlot(new BrewingScreenHandler.PotionSlot(playerInventory.player, inventory, 1, 79, 53));
		this.addSlot(new BrewingScreenHandler.PotionSlot(playerInventory.player, inventory, 2, 102, 46));
		this.ingredientSlot = this.addSlot(new BrewingScreenHandler.FuelSlot(inventory, 3, 79, 17));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; k++) {
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
		}
	}

	@Override
	public void addListener(ScreenHandlerListener listener) {
		super.addListener(listener);
		listener.onScreenHandlerInventoryUpdate(this, this.inventory);
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();

		for (int i = 0; i < this.listeners.size(); i++) {
			ScreenHandlerListener screenHandlerListener = (ScreenHandlerListener)this.listeners.get(i);
			if (this.field_4094 != this.inventory.getProperty(0)) {
				screenHandlerListener.onScreenHandlerPropertyUpdate(this, 0, this.inventory.getProperty(0));
			}
		}

		this.field_4094 = this.inventory.getProperty(0);
	}

	@Override
	public void setProperty(int id, int value) {
		this.inventory.setProperty(id, value);
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
			if ((invSlot < 0 || invSlot > 2) && invSlot != 3) {
				if (!this.ingredientSlot.hasStack() && this.ingredientSlot.canInsert(itemStack2)) {
					if (!this.insertItem(itemStack2, 3, 4, false)) {
						return null;
					}
				} else if (BrewingScreenHandler.PotionSlot.matches(itemStack)) {
					if (!this.insertItem(itemStack2, 0, 3, false)) {
						return null;
					}
				} else if (invSlot >= 4 && invSlot < 31) {
					if (!this.insertItem(itemStack2, 31, 40, false)) {
						return null;
					}
				} else if (invSlot >= 31 && invSlot < 40) {
					if (!this.insertItem(itemStack2, 4, 31, false)) {
						return null;
					}
				} else if (!this.insertItem(itemStack2, 4, 40, false)) {
					return null;
				}
			} else {
				if (!this.insertItem(itemStack2, 4, 40, true)) {
					return null;
				}

				slot.onStackChanged(itemStack2, itemStack);
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

	class FuelSlot extends Slot {
		public FuelSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return stack != null ? stack.getItem().hasStatusEffectString(stack) : false;
		}

		@Override
		public int getMaxStackAmount() {
			return 64;
		}
	}

	static class PotionSlot extends Slot {
		private PlayerEntity player;

		public PotionSlot(PlayerEntity playerEntity, Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
			this.player = playerEntity;
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return matches(stack);
		}

		@Override
		public int getMaxStackAmount() {
			return 1;
		}

		@Override
		public void onTakeItem(PlayerEntity player, ItemStack stack) {
			if (stack.getItem() == Items.POTION && stack.getData() > 0) {
				this.player.incrementStat(AchievementsAndCriterions.POTION);
			}

			super.onTakeItem(player, stack);
		}

		public static boolean matches(ItemStack stack) {
			return stack != null && (stack.getItem() == Items.POTION || stack.getItem() == Items.GLASS_BOTTLE);
		}
	}
}
