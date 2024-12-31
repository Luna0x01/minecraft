package net.minecraft.inventory.slot;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class Slot {
	private final int invSlot;
	public final Inventory inventory;
	public int id;
	public int x;
	public int y;

	public Slot(Inventory inventory, int i, int j, int k) {
		this.inventory = inventory;
		this.invSlot = i;
		this.x = j;
		this.y = k;
	}

	public void onStackChanged(ItemStack originalItem, ItemStack newItem) {
		if (originalItem != null && newItem != null) {
			if (originalItem.getItem() == newItem.getItem()) {
				int i = newItem.count - originalItem.count;
				if (i > 0) {
					this.onCrafted(originalItem, i);
				}
			}
		}
	}

	protected void onCrafted(ItemStack stack, int amount) {
	}

	protected void onCrafted(ItemStack stack) {
	}

	public void onTakeItem(PlayerEntity player, ItemStack stack) {
		this.markDirty();
	}

	public boolean canInsert(@Nullable ItemStack stack) {
		return true;
	}

	@Nullable
	public ItemStack getStack() {
		return this.inventory.getInvStack(this.invSlot);
	}

	public boolean hasStack() {
		return this.getStack() != null;
	}

	public void setStack(@Nullable ItemStack stack) {
		this.inventory.setInvStack(this.invSlot, stack);
		this.markDirty();
	}

	public void markDirty() {
		this.inventory.markDirty();
	}

	public int getMaxStackAmount() {
		return this.inventory.getInvMaxStackAmount();
	}

	public int getMaxStackAmount(ItemStack stack) {
		return this.getMaxStackAmount();
	}

	@Nullable
	public String getBackgroundSprite() {
		return null;
	}

	public ItemStack takeStack(int amount) {
		return this.inventory.takeInvStack(this.invSlot, amount);
	}

	public boolean equals(Inventory inventory, int slot) {
		return inventory == this.inventory && slot == this.invSlot;
	}

	public boolean canTakeItems(PlayerEntity playerEntity) {
		return true;
	}

	public boolean doDrawHoveringEffect() {
		return true;
	}
}
