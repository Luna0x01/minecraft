package net.minecraft.container;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class Slot {
	private final int invSlot;
	public final Inventory inventory;
	public int id;
	public int xPosition;
	public int yPosition;

	public Slot(Inventory inventory, int i, int j, int k) {
		this.inventory = inventory;
		this.invSlot = i;
		this.xPosition = j;
		this.yPosition = k;
	}

	public void onStackChanged(ItemStack itemStack, ItemStack itemStack2) {
		int i = itemStack2.getCount() - itemStack.getCount();
		if (i > 0) {
			this.onCrafted(itemStack2, i);
		}
	}

	protected void onCrafted(ItemStack itemStack, int i) {
	}

	protected void onTake(int i) {
	}

	protected void onCrafted(ItemStack itemStack) {
	}

	public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
		this.markDirty();
		return itemStack;
	}

	public boolean canInsert(ItemStack itemStack) {
		return true;
	}

	public ItemStack getStack() {
		return this.inventory.getInvStack(this.invSlot);
	}

	public boolean hasStack() {
		return !this.getStack().isEmpty();
	}

	public void setStack(ItemStack itemStack) {
		this.inventory.setInvStack(this.invSlot, itemStack);
		this.markDirty();
	}

	public void markDirty() {
		this.inventory.markDirty();
	}

	public int getMaxStackAmount() {
		return this.inventory.getInvMaxStackAmount();
	}

	public int getMaxStackAmount(ItemStack itemStack) {
		return this.getMaxStackAmount();
	}

	@Nullable
	public String getBackgroundSprite() {
		return null;
	}

	public ItemStack takeStack(int i) {
		return this.inventory.takeInvStack(this.invSlot, i);
	}

	public boolean canTakeItems(PlayerEntity playerEntity) {
		return true;
	}

	public boolean doDrawHoveringEffect() {
		return true;
	}
}
