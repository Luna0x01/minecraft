package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CraftingInventory implements Inventory {
	private final ItemStack[] stacks;
	private final int width;
	private final int height;
	private final ScreenHandler screenHandler;

	public CraftingInventory(ScreenHandler screenHandler, int i, int j) {
		int k = i * j;
		this.stacks = new ItemStack[k];
		this.screenHandler = screenHandler;
		this.width = i;
		this.height = j;
	}

	@Override
	public int getInvSize() {
		return this.stacks.length;
	}

	@Nullable
	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= this.getInvSize() ? null : this.stacks[slot];
	}

	@Nullable
	public ItemStack getStackAt(int width, int height) {
		return width >= 0 && width < this.width && height >= 0 && height <= this.height ? this.getInvStack(width + height * this.width) : null;
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

	@Nullable
	@Override
	public ItemStack removeInvStack(int slot) {
		return class_2960.method_12932(this.stacks, slot);
	}

	@Nullable
	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack itemStack = class_2960.method_12933(this.stacks, slot, amount);
		if (itemStack != null) {
			this.screenHandler.onContentChanged(this);
		}

		return itemStack;
	}

	@Override
	public void setInvStack(int slot, @Nullable ItemStack stack) {
		this.stacks[slot] = stack;
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
		for (int i = 0; i < this.stacks.length; i++) {
			this.stacks[i] = null;
		}
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}
}
