package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CraftingResultInventory implements Inventory {
	private final ItemStack[] stacks = new ItemStack[1];

	@Override
	public int getInvSize() {
		return 1;
	}

	@Nullable
	@Override
	public ItemStack getInvStack(int slot) {
		return this.stacks[0];
	}

	@Override
	public String getTranslationKey() {
		return "Result";
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
	public ItemStack takeInvStack(int slot, int amount) {
		return class_2960.method_12932(this.stacks, 0);
	}

	@Nullable
	@Override
	public ItemStack removeInvStack(int slot) {
		return class_2960.method_12932(this.stacks, 0);
	}

	@Override
	public void setInvStack(int slot, @Nullable ItemStack stack) {
		this.stacks[0] = stack;
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
}
