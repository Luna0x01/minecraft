package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class SimpleInventory implements Inventory {
	private String name;
	private int size;
	private ItemStack[] inventory;
	private List<SimpleInventoryListener> listeners;
	private boolean hasCustomName;

	public SimpleInventory(String string, boolean bl, int i) {
		this.name = string;
		this.hasCustomName = bl;
		this.size = i;
		this.inventory = new ItemStack[i];
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

	@Nullable
	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= 0 && slot < this.inventory.length ? this.inventory[slot] : null;
	}

	@Nullable
	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack itemStack = class_2960.method_12933(this.inventory, slot, amount);
		if (itemStack != null) {
			this.markDirty();
		}

		return itemStack;
	}

	@Nullable
	public ItemStack fillInventoryWith(ItemStack stack) {
		ItemStack itemStack = stack.copy();

		for (int i = 0; i < this.size; i++) {
			ItemStack itemStack2 = this.getInvStack(i);
			if (itemStack2 == null) {
				this.setInvStack(i, itemStack);
				this.markDirty();
				return null;
			}

			if (ItemStack.equalsIgnoreNbt(itemStack2, itemStack)) {
				int j = Math.min(this.getInvMaxStackAmount(), itemStack2.getMaxCount());
				int k = Math.min(itemStack.count, j - itemStack2.count);
				if (k > 0) {
					itemStack2.count += k;
					itemStack.count -= k;
					if (itemStack.count <= 0) {
						this.markDirty();
						return null;
					}
				}
			}
		}

		if (itemStack.count != stack.count) {
			this.markDirty();
		}

		return itemStack;
	}

	@Nullable
	@Override
	public ItemStack removeInvStack(int slot) {
		if (this.inventory[slot] != null) {
			ItemStack itemStack = this.inventory[slot];
			this.inventory[slot] = null;
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public void setInvStack(int slot, @Nullable ItemStack stack) {
		this.inventory[slot] = stack;
		if (stack != null && stack.count > this.getInvMaxStackAmount()) {
			stack.count = this.getInvMaxStackAmount();
		}

		this.markDirty();
	}

	@Override
	public int getInvSize() {
		return this.size;
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
				((SimpleInventoryListener)this.listeners.get(i)).onChanged(this);
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
		for (int i = 0; i < this.inventory.length; i++) {
			this.inventory[i] = null;
		}
	}
}
