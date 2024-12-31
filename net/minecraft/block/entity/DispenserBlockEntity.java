package net.minecraft.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.Generic3x3ScreenHandler;
import net.minecraft.screen.ScreenHandler;

public class DispenserBlockEntity extends class_2737 implements Inventory {
	private static final Random RANDOM = new Random();
	private ItemStack[] items = new ItemStack[9];
	protected String customName;

	@Override
	public int getInvSize() {
		return 9;
	}

	@Nullable
	@Override
	public ItemStack getInvStack(int slot) {
		this.method_11662(null);
		return this.items[slot];
	}

	@Nullable
	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		this.method_11662(null);
		ItemStack itemStack = class_2960.method_12933(this.items, slot, amount);
		if (itemStack != null) {
			this.markDirty();
		}

		return itemStack;
	}

	@Nullable
	@Override
	public ItemStack removeInvStack(int slot) {
		this.method_11662(null);
		return class_2960.method_12932(this.items, slot);
	}

	public int chooseNonEmptySlot() {
		this.method_11662(null);
		int i = -1;
		int j = 1;

		for (int k = 0; k < this.items.length; k++) {
			if (this.items[k] != null && RANDOM.nextInt(j++) == 0) {
				i = k;
			}
		}

		return i;
	}

	@Override
	public void setInvStack(int slot, @Nullable ItemStack stack) {
		this.method_11662(null);
		this.items[slot] = stack;
		if (stack != null && stack.count > this.getInvMaxStackAmount()) {
			stack.count = this.getInvMaxStackAmount();
		}

		this.markDirty();
	}

	public int addToFirstFreeSlot(ItemStack stack) {
		for (int i = 0; i < this.items.length; i++) {
			if (this.items[i] == null || this.items[i].getItem() == null) {
				this.setInvStack(i, stack);
				return i;
			}
		}

		return -1;
	}

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.customName : "container.dispenser";
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		if (!this.method_11661(nbt)) {
			NbtList nbtList = nbt.getList("Items", 10);
			this.items = new ItemStack[this.getInvSize()];

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				int j = nbtCompound.getByte("Slot") & 255;
				if (j >= 0 && j < this.items.length) {
					this.items[j] = ItemStack.fromNbt(nbtCompound);
				}
			}
		}

		if (nbt.contains("CustomName", 8)) {
			this.customName = nbt.getString("CustomName");
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (!this.method_11663(nbt)) {
			NbtList nbtList = new NbtList();

			for (int i = 0; i < this.items.length; i++) {
				if (this.items[i] != null) {
					NbtCompound nbtCompound = new NbtCompound();
					nbtCompound.putByte("Slot", (byte)i);
					this.items[i].toNbt(nbtCompound);
					nbtList.add(nbtCompound);
				}
			}

			nbt.put("Items", nbtList);
		}

		if (this.hasCustomName()) {
			nbt.putString("CustomName", this.customName);
		}

		return nbt;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
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
	public String getId() {
		return "minecraft:dispenser";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		this.method_11662(player);
		return new Generic3x3ScreenHandler(inventory, this);
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
		this.method_11662(null);

		for (int i = 0; i < this.items.length; i++) {
			this.items[i] = null;
		}
	}
}
