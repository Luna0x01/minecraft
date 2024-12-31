package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.Generic3x3ScreenHandler;
import net.minecraft.screen.ScreenHandler;

public class DispenserBlockEntity extends LockableContainerBlockEntity implements Inventory {
	private static final Random RANDOM = new Random();
	private ItemStack[] items = new ItemStack[9];
	protected String customName;

	@Override
	public int getInvSize() {
		return 9;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return this.items[slot];
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		if (this.items[slot] != null) {
			if (this.items[slot].count <= amount) {
				ItemStack itemStack = this.items[slot];
				this.items[slot] = null;
				this.markDirty();
				return itemStack;
			} else {
				ItemStack itemStack2 = this.items[slot].split(amount);
				if (this.items[slot].count == 0) {
					this.items[slot] = null;
				}

				this.markDirty();
				return itemStack2;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		if (this.items[slot] != null) {
			ItemStack itemStack = this.items[slot];
			this.items[slot] = null;
			return itemStack;
		} else {
			return null;
		}
	}

	public int chooseNonEmptySlot() {
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
	public void setInvStack(int slot, ItemStack stack) {
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
		NbtList nbtList = nbt.getList("Items", 10);
		this.items = new ItemStack[this.getInvSize()];

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot") & 255;
			if (j >= 0 && j < this.items.length) {
				this.items[j] = ItemStack.fromNbt(nbtCompound);
			}
		}

		if (nbt.contains("CustomName", 8)) {
			this.customName = nbt.getString("CustomName");
		}
	}

	@Override
	public void toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
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
		if (this.hasCustomName()) {
			nbt.putString("CustomName", this.customName);
		}
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
		for (int i = 0; i < this.items.length; i++) {
			this.items[i] = null;
		}
	}
}
