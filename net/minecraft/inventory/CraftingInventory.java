package net.minecraft.inventory;

import net.minecraft.class_2960;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

public class CraftingInventory implements Inventory {
	private final DefaultedList<ItemStack> field_15100;
	private final int width;
	private final int height;
	private final ScreenHandler screenHandler;

	public CraftingInventory(ScreenHandler screenHandler, int i, int j) {
		this.field_15100 = DefaultedList.ofSize(i * j, ItemStack.EMPTY);
		this.screenHandler = screenHandler;
		this.width = i;
		this.height = j;
	}

	@Override
	public int getInvSize() {
		return this.field_15100.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.field_15100) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= this.getInvSize() ? ItemStack.EMPTY : this.field_15100.get(slot);
	}

	public ItemStack getStackAt(int width, int height) {
		return width >= 0 && width < this.width && height >= 0 && height <= this.height ? this.getInvStack(width + height * this.width) : ItemStack.EMPTY;
	}

	@Override
	public String getTranslationKey() {
		return "container.crafting";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public Text getName() {
		return (Text)(this.hasCustomName() ? new LiteralText(this.getTranslationKey()) : new TranslatableText(this.getTranslationKey()));
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return class_2960.method_13925(this.field_15100, slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack itemStack = class_2960.method_13926(this.field_15100, slot, amount);
		if (!itemStack.isEmpty()) {
			this.screenHandler.onContentChanged(this);
		}

		return itemStack;
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.field_15100.set(slot, stack);
		this.screenHandler.onContentChanged(this);
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return true;
	}

	@Override
	public void onInvOpen(PlayerEntity player) {
	}

	@Override
	public void onInvClose(PlayerEntity player) {
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public int getProperty(int key) {
		return 0;
	}

	@Override
	public void setProperty(int id, int value) {
	}

	@Override
	public int getProperties() {
		return 0;
	}

	@Override
	public void clear() {
		this.field_15100.clear();
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}
}
