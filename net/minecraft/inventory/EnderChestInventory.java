package net.minecraft.inventory;

import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class EnderChestInventory extends SimpleInventory {
	private EnderChestBlockEntity blockEntity;

	public EnderChestInventory() {
		super("container.enderchest", false, 27);
	}

	public void setBlockEntity(EnderChestBlockEntity blockEntity) {
		this.blockEntity = blockEntity;
	}

	public void readNbtList(NbtList nbtList) {
		for (int i = 0; i < this.getInvSize(); i++) {
			this.setInvStack(i, ItemStack.EMPTY);
		}

		for (int j = 0; j < nbtList.size(); j++) {
			NbtCompound nbtCompound = nbtList.getCompound(j);
			int k = nbtCompound.getByte("Slot") & 255;
			if (k >= 0 && k < this.getInvSize()) {
				this.setInvStack(k, new ItemStack(nbtCompound));
			}
		}
	}

	public NbtList toNbtList() {
		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.getInvSize(); i++) {
			ItemStack itemStack = this.getInvStack(i);
			if (!itemStack.isEmpty()) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Slot", (byte)i);
				itemStack.toNbt(nbtCompound);
				nbtList.add(nbtCompound);
			}
		}

		return nbtList;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.blockEntity != null && !this.blockEntity.canPlayerUse(player) ? false : super.canPlayerUseInv(player);
	}

	@Override
	public void onInvOpen(PlayerEntity player) {
		if (this.blockEntity != null) {
			this.blockEntity.onOpen();
		}

		super.onInvOpen(player);
	}

	@Override
	public void onInvClose(PlayerEntity player) {
		if (this.blockEntity != null) {
			this.blockEntity.onClose();
		}

		super.onInvClose(player);
		this.blockEntity = null;
	}
}
