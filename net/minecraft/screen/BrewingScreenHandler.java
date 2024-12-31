package net.minecraft.screen;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.entity.effect.StatusEffectStrings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;

public class BrewingScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final Slot ingredientSlot;
	private int field_4094;
	private int field_12262;

	public BrewingScreenHandler(PlayerInventory playerInventory, Inventory inventory) {
		this.inventory = inventory;
		this.addSlot(new BrewingScreenHandler.PotionSlot(inventory, 0, 56, 51));
		this.addSlot(new BrewingScreenHandler.PotionSlot(inventory, 1, 79, 58));
		this.addSlot(new BrewingScreenHandler.PotionSlot(inventory, 2, 102, 51));
		this.ingredientSlot = this.addSlot(new BrewingScreenHandler.FuelSlot(inventory, 3, 79, 17));
		this.addSlot(new BrewingScreenHandler.class_2678(inventory, 4, 17, 17));

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

			if (this.field_12262 != this.inventory.getProperty(1)) {
				screenHandlerListener.onScreenHandlerPropertyUpdate(this, 1, this.inventory.getProperty(1));
			}
		}

		this.field_4094 = this.inventory.getProperty(0);
		this.field_12262 = this.inventory.getProperty(1);
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
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if ((invSlot < 0 || invSlot > 2) && invSlot != 3 && invSlot != 4) {
				if (this.ingredientSlot.canInsert(itemStack2)) {
					if (!this.insertItem(itemStack2, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if (BrewingScreenHandler.PotionSlot.matches(itemStack) && itemStack.getCount() == 1) {
					if (!this.insertItem(itemStack2, 0, 3, false)) {
						return ItemStack.EMPTY;
					}
				} else if (BrewingScreenHandler.class_2678.method_11350(itemStack)) {
					if (!this.insertItem(itemStack2, 4, 5, false)) {
						return ItemStack.EMPTY;
					}
				} else if (invSlot >= 5 && invSlot < 32) {
					if (!this.insertItem(itemStack2, 32, 41, false)) {
						return ItemStack.EMPTY;
					}
				} else if (invSlot >= 32 && invSlot < 41) {
					if (!this.insertItem(itemStack2, 5, 32, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(itemStack2, 5, 41, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!this.insertItem(itemStack2, 5, 41, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.method_3298(player, itemStack2);
		}

		return itemStack;
	}

	static class FuelSlot extends Slot {
		public FuelSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return StatusEffectStrings.method_11417(stack);
		}

		@Override
		public int getMaxStackAmount() {
			return 64;
		}
	}

	static class PotionSlot extends Slot {
		public PotionSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
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
		public ItemStack method_3298(PlayerEntity playerEntity, ItemStack itemStack) {
			Potion potion = PotionUtil.getPotion(itemStack);
			if (playerEntity instanceof ServerPlayerEntity) {
				AchievementsAndCriterions.field_16338.method_15062((ServerPlayerEntity)playerEntity, potion);
			}

			super.method_3298(playerEntity, itemStack);
			return itemStack;
		}

		public static boolean matches(ItemStack stack) {
			Item item = stack.getItem();
			return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE;
		}
	}

	static class class_2678 extends Slot {
		public class_2678(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return method_11350(stack);
		}

		public static boolean method_11350(ItemStack itemStack) {
			return itemStack.getItem() == Items.BLAZE_POWDER;
		}

		@Override
		public int getMaxStackAmount() {
			return 64;
		}
	}
}
