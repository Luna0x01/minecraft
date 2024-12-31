package net.minecraft.inventory;

import net.minecraft.class_2960;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

public class CraftingResultInventory implements Inventory {
	private final DefaultedList<ItemStack> field_15105 = DefaultedList.ofSize(1, ItemStack.EMPTY);

	@Override
	public int getInvSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.field_15105) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return this.field_15105.get(0);
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

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		return class_2960.method_13925(this.field_15105, 0);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return class_2960.method_13925(this.field_15105, 0);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.field_15105.set(0, stack);
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
		this.field_15105.clear();
	}
}
