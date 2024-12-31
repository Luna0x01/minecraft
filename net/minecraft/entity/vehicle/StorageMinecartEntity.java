package net.minecraft.entity.vehicle;

import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ScreenHandlerLock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ItemScatterer;
import net.minecraft.world.World;

public abstract class StorageMinecartEntity extends AbstractMinecartEntity implements LockableScreenHandlerFactory {
	private ItemStack[] stacks = new ItemStack[36];
	private boolean field_6145 = true;

	public StorageMinecartEntity(World world) {
		super(world);
	}

	public StorageMinecartEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			ItemScatterer.spawn(this.world, this, this);
		}
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return this.stacks[slot];
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		if (this.stacks[slot] != null) {
			if (this.stacks[slot].count <= amount) {
				ItemStack itemStack = this.stacks[slot];
				this.stacks[slot] = null;
				return itemStack;
			} else {
				ItemStack itemStack2 = this.stacks[slot].split(amount);
				if (this.stacks[slot].count == 0) {
					this.stacks[slot] = null;
				}

				return itemStack2;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		if (this.stacks[slot] != null) {
			ItemStack itemStack = this.stacks[slot];
			this.stacks[slot] = null;
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.stacks[slot] = stack;
		if (stack != null && stack.count > this.getInvMaxStackAmount()) {
			stack.count = this.getInvMaxStackAmount();
		}
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.removed ? false : !(player.squaredDistanceTo(this) > 64.0);
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
	public String getTranslationKey() {
		return this.hasCustomName() ? this.getCustomName() : "container.minecart";
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public void teleportToDimension(int dimensionId) {
		this.field_6145 = false;
		super.teleportToDimension(dimensionId);
	}

	@Override
	public void remove() {
		if (this.field_6145) {
			ItemScatterer.spawn(this.world, this, this);
		}

		super.remove();
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.stacks.length; i++) {
			if (this.stacks[i] != null) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Slot", (byte)i);
				this.stacks[i].toNbt(nbtCompound);
				nbtList.add(nbtCompound);
			}
		}

		nbt.put("Items", nbtList);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		NbtList nbtList = nbt.getList("Items", 10);
		this.stacks = new ItemStack[this.getInvSize()];

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot") & 255;
			if (j >= 0 && j < this.stacks.length) {
				this.stacks[j] = ItemStack.fromNbt(nbtCompound);
			}
		}
	}

	@Override
	public boolean openInventory(PlayerEntity player) {
		if (!this.world.isClient) {
			player.openInventory(this);
		}

		return true;
	}

	@Override
	protected void applySlowdown() {
		int i = 15 - ScreenHandler.calculateComparatorOutput(this);
		float f = 0.98F + (float)i * 0.001F;
		this.velocityX *= (double)f;
		this.velocityY *= 0.0;
		this.velocityZ *= (double)f;
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
	public boolean hasLock() {
		return false;
	}

	@Override
	public void setLock(ScreenHandlerLock lock) {
	}

	@Override
	public ScreenHandlerLock getLock() {
		return ScreenHandlerLock.NONE;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.stacks.length; i++) {
			this.stacks[i] = null;
		}
	}
}
