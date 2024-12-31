package net.minecraft.screen;

import net.minecraft.class_3135;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HorseScreenHandler extends ScreenHandler {
	private final Inventory playerInv;
	private final AbstractHorseEntity field_15101;

	public HorseScreenHandler(Inventory inventory, Inventory inventory2, AbstractHorseEntity abstractHorseEntity, PlayerEntity playerEntity) {
		this.playerInv = inventory2;
		this.field_15101 = abstractHorseEntity;
		int i = 3;
		inventory2.onInvOpen(playerEntity);
		int j = -18;
		this.addSlot(new Slot(inventory2, 0, 8, 18) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() == Items.SADDLE && !this.hasStack() && abstractHorseEntity.method_13974();
			}

			@Override
			public boolean doDrawHoveringEffect() {
				return abstractHorseEntity.method_13974();
			}
		});
		this.addSlot(new Slot(inventory2, 1, 8, 36) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return abstractHorseEntity.method_14001(stack);
			}

			@Override
			public boolean doDrawHoveringEffect() {
				return abstractHorseEntity.method_13984();
			}

			@Override
			public int getMaxStackAmount() {
				return 1;
			}
		});
		if (abstractHorseEntity instanceof class_3135 && ((class_3135)abstractHorseEntity).method_13963()) {
			for (int k = 0; k < 3; k++) {
				for (int l = 0; l < ((class_3135)abstractHorseEntity).method_13965(); l++) {
					this.addSlot(new Slot(inventory2, 2 + l + k * ((class_3135)abstractHorseEntity).method_13965(), 80 + l * 18, 18 + k * 18));
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
		return this.playerInv.canPlayerUseInv(player) && this.field_15101.isAlive() && this.field_15101.distanceTo(player) < 8.0F;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot < this.playerInv.getInvSize()) {
				if (!this.insertItem(itemStack2, this.playerInv.getInvSize(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(1).canInsert(itemStack2) && !this.getSlot(1).hasStack()) {
				if (!this.insertItem(itemStack2, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(0).canInsert(itemStack2)) {
				if (!this.insertItem(itemStack2, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.playerInv.getInvSize() <= 2 || !this.insertItem(itemStack2, 2, this.playerInv.getInvSize(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
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
