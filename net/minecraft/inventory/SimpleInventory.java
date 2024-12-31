package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.class_2960;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

public class SimpleInventory implements Inventory {
	private String name;
	private final int size;
	private final DefaultedList<ItemStack> content;
	private List<SimpleInventoryListener> listeners;
	private boolean hasCustomName;

	public SimpleInventory(String string, boolean bl, int i) {
		this.name = string;
		this.hasCustomName = bl;
		this.size = i;
		this.content = DefaultedList.ofSize(i, ItemStack.EMPTY);
	}

	public SimpleInventory(Text text, int i) {
		this(text.asUnformattedString(), true, i);
	}

	public void addListener(SimpleInventoryListener listener) {
		if (this.listeners == null) {
			this.listeners = Lists.newArrayList();
		}

		this.listeners.add(listener);
	}

	public void removeListener(SimpleInventoryListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= 0 && slot < this.content.size() ? this.content.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack itemStack = class_2960.method_13926(this.content, slot, amount);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	public ItemStack fillInventoryWith(ItemStack stack) {
		ItemStack itemStack = stack.copy();

		for (int i = 0; i < this.size; i++) {
			ItemStack itemStack2 = this.getInvStack(i);
			if (itemStack2.isEmpty()) {
				this.setInvStack(i, itemStack);
				this.markDirty();
				return ItemStack.EMPTY;
			}

			if (ItemStack.equalsIgnoreNbt(itemStack2, itemStack)) {
				int j = Math.min(this.getInvMaxStackAmount(), itemStack2.getMaxCount());
				int k = Math.min(itemStack.getCount(), j - itemStack2.getCount());
				if (k > 0) {
					itemStack2.increment(k);
					itemStack.decrement(k);
					if (itemStack.isEmpty()) {
						this.markDirty();
						return ItemStack.EMPTY;
					}
				}
			}
		}

		if (itemStack.getCount() != stack.getCount()) {
			this.markDirty();
		}

		return itemStack;
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		ItemStack itemStack = this.content.get(slot);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.content.set(slot, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.content.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}

		this.markDirty();
	}

	@Override
	public int getInvSize() {
		return this.size;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.content) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getTranslationKey() {
		return this.name;
	}

	@Override
	public boolean hasCustomName() {
		return this.hasCustomName;
	}

	public void setName(String name) {
		this.hasCustomName = true;
		this.name = name;
	}

	@Override
	public Text getName() {
		return (Text)(this.hasCustomName() ? new LiteralText(this.getTranslationKey()) : new TranslatableText(this.getTranslationKey()));
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public void markDirty() {
		if (this.listeners != null) {
			for (int i = 0; i < this.listeners.size(); i++) {
				((SimpleInventoryListener)this.listeners.get(i)).method_13928(this);
			}
		}
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
		this.content.clear();
	}
}
