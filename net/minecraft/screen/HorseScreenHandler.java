package net.minecraft.screen;

import javax.annotation.Nullable;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.HorseArmorType;

public class HorseScreenHandler extends ScreenHandler {
	private final Inventory playerInv;
	private final HorseBaseEntity entity;

	public HorseScreenHandler(Inventory inventory, Inventory inventory2, HorseBaseEntity horseBaseEntity, PlayerEntity playerEntity) {
		this.playerInv = inventory2;
		this.entity = horseBaseEntity;
		int i = 3;
		inventory2.onInvOpen(playerEntity);
		int j = -18;
		this.addSlot(new Slot(inventory2, 0, 8, 18) {
			@Override
			public boolean canInsert(@Nullable ItemStack stack) {
				return super.canInsert(stack) && stack.getItem() == Items.SADDLE && !this.hasStack();
			}
		});
		this.addSlot(new Slot(inventory2, 1, 8, 36) {
			@Override
			public boolean canInsert(@Nullable ItemStack stack) {
				return super.canInsert(stack) && horseBaseEntity.method_13129().method_13152() && HorseArmorType.method_13139(stack.getItem());
			}

			@Override
			public boolean doDrawHoveringEffect() {
				return horseBaseEntity.method_13129().method_13152();
			}
		});
		if (horseBaseEntity.hasChest()) {
			for (int k = 0; k < 3; k++) {
				for (int l = 0; l < 5; l++) {
					this.addSlot(new Slot(inventory2, 2 + l + k * 5, 80 + l * 18, 18 + k * 18));
				}
			}
		}

		for (int m = 0; m < 3; m++) {
			for (int n = 0; n < 9; n++) {
				this.addSlot(new Slot(inventory, n + m * 9 + 9, 8 + n * 18, 102 + m * 18 + -18));
			}
		}

		for (int o = 0; o < 9; o++) {
			this.addSlot(new Slot(inventory, o, 8 + o * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.playerInv.canPlayerUseInv(player) && this.entity.isAlive() && this.entity.distanceTo(player) < 8.0F;
	}

	@Nullable
	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = null;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot < this.playerInv.getInvSize()) {
				if (!this.insertItem(itemStack2, this.playerInv.getInvSize(), this.slots.size(), true)) {
					return null;
				}
			} else if (this.getSlot(1).canInsert(itemStack2) && !this.getSlot(1).hasStack()) {
				if (!this.insertItem(itemStack2, 1, 2, false)) {
					return null;
				}
			} else if (this.getSlot(0).canInsert(itemStack2)) {
				if (!this.insertItem(itemStack2, 0, 1, false)) {
					return null;
				}
			} else if (this.playerInv.getInvSize() <= 2 || !this.insertItem(itemStack2, 2, this.playerInv.getInvSize(), false)) {
				return null;
			}

			if (itemStack2.count == 0) {
				slot.setStack(null);
			} else {
				slot.markDirty();
			}
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.playerInv.onInvClose(player);
	}
}
