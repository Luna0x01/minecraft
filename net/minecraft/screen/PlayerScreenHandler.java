package net.minecraft.screen;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.CraftingResultSlot;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeDispatcher;

public class PlayerScreenHandler extends ScreenHandler {
	public CraftingInventory craftingInventory = new CraftingInventory(this, 2, 2);
	public Inventory craftingResultInventory = new CraftingResultInventory();
	public boolean onServer;
	private final PlayerEntity owner;

	public PlayerScreenHandler(PlayerInventory playerInventory, boolean bl, PlayerEntity playerEntity) {
		this.onServer = bl;
		this.owner = playerEntity;
		this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInventory, this.craftingResultInventory, 0, 144, 36));

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new Slot(this.craftingInventory, j + i * 2, 88 + j * 18, 26 + i * 18));
			}
		}

		for (int k = 0; k < 4; k++) {
			final int l = k;
			this.addSlot(new Slot(playerInventory, playerInventory.getInvSize() - 1 - k, 8, 8 + k * 18) {
				@Override
				public int getMaxStackAmount() {
					return 1;
				}

				@Override
				public boolean canInsert(ItemStack stack) {
					if (stack == null) {
						return false;
					} else if (stack.getItem() instanceof ArmorItem) {
						return ((ArmorItem)stack.getItem()).slot == l;
					} else {
						return stack.getItem() != Item.fromBlock(Blocks.PUMPKIN) && stack.getItem() != Items.SKULL ? false : l == 0;
					}
				}

				@Override
				public String getBackgroundSprite() {
					return ArmorItem.EMPTY[l];
				}
			});
		}

		for (int m = 0; m < 3; m++) {
			for (int n = 0; n < 9; n++) {
				this.addSlot(new Slot(playerInventory, n + (m + 1) * 9, 8 + n * 18, 84 + m * 18));
			}
		}

		for (int o = 0; o < 9; o++) {
			this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
		}

		this.onContentChanged(this.craftingInventory);
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.craftingResultInventory.setInvStack(0, RecipeDispatcher.getInstance().matches(this.craftingInventory, this.owner.world));
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);

		for (int i = 0; i < 4; i++) {
			ItemStack itemStack = this.craftingInventory.removeInvStack(i);
			if (itemStack != null) {
				player.dropItem(itemStack, false);
			}
		}

		this.craftingResultInventory.setInvStack(0, null);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = null;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot == 0) {
				if (!this.insertItem(itemStack2, 9, 45, true)) {
					return null;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (invSlot >= 1 && invSlot < 5) {
				if (!this.insertItem(itemStack2, 9, 45, false)) {
					return null;
				}
			} else if (invSlot >= 5 && invSlot < 9) {
				if (!this.insertItem(itemStack2, 9, 45, false)) {
					return null;
				}
			} else if (itemStack.getItem() instanceof ArmorItem && !((Slot)this.slots.get(5 + ((ArmorItem)itemStack.getItem()).slot)).hasStack()) {
				int i = 5 + ((ArmorItem)itemStack.getItem()).slot;
				if (!this.insertItem(itemStack2, i, i + 1, false)) {
					return null;
				}
			} else if (invSlot >= 9 && invSlot < 36) {
				if (!this.insertItem(itemStack2, 36, 45, false)) {
					return null;
				}
			} else if (invSlot >= 36 && invSlot < 45) {
				if (!this.insertItem(itemStack2, 9, 36, false)) {
					return null;
				}
			} else if (!this.insertItem(itemStack2, 9, 45, false)) {
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

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.craftingResultInventory && super.canInsertIntoSlot(stack, slot);
	}
}
