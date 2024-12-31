package net.minecraft.screen;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.CraftingResultSlot;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingScreenHandler extends ScreenHandler {
	public CraftingInventory craftingInv = new CraftingInventory(this, 3, 3);
	public CraftingResultInventory field_15644 = new CraftingResultInventory();
	private final World world;
	private final BlockPos pos;
	private final PlayerEntity field_15645;

	public CraftingScreenHandler(PlayerInventory playerInventory, World world, BlockPos blockPos) {
		this.world = world;
		this.pos = blockPos;
		this.field_15645 = playerInventory.player;
		this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInv, this.field_15644, 0, 124, 35));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlot(new Slot(this.craftingInv, j + i * 3, 30 + j * 18, 17 + i * 18));
			}
		}

		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 9; l++) {
				this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
			}
		}

		for (int m = 0; m < 9; m++) {
			this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.method_14205(this.world, this.field_15645, this.craftingInv, this.field_15644);
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		if (!this.world.isClient) {
			this.method_14204(player, this.world, this.craftingInv);
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.world.getBlockState(this.pos).getBlock() != Blocks.CRAFTING_TABLE
			? false
			: player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot == 0) {
				itemStack2.getItem().onCraft(itemStack2, this.world, player);
				if (!this.insertItem(itemStack2, 10, 46, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (invSlot >= 10 && invSlot < 37) {
				if (!this.insertItem(itemStack2, 37, 46, false)) {
					return ItemStack.EMPTY;
				}
			} else if (invSlot >= 37 && invSlot < 46) {
				if (!this.insertItem(itemStack2, 10, 37, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 10, 46, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemStack3 = slot.method_3298(player, itemStack2);
			if (invSlot == 0) {
				player.dropItem(itemStack3, false);
			}
		}

		return itemStack;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.field_15644 && super.canInsertIntoSlot(stack, slot);
	}
}
