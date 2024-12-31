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
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingScreenHandler extends ScreenHandler {
	public CraftingInventory craftingInv = new CraftingInventory(this, 3, 3);
	public Inventory resultInv = new CraftingResultInventory();
	private World world;
	private BlockPos pos;

	public CraftingScreenHandler(PlayerInventory playerInventory, World world, BlockPos blockPos) {
		this.world = world;
		this.pos = blockPos;
		this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInv, this.resultInv, 0, 124, 35));

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

		this.onContentChanged(this.craftingInv);
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.resultInv.setInvStack(0, RecipeDispatcher.getInstance().matches(this.craftingInv, this.world));
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		if (!this.world.isClient) {
			for (int i = 0; i < 9; i++) {
				ItemStack itemStack = this.craftingInv.removeInvStack(i);
				if (itemStack != null) {
					player.dropItem(itemStack, false);
				}
			}
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.world.getBlockState(this.pos).getBlock() != Blocks.CRAFTING_TABLE
			? false
			: !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = null;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot == 0) {
				if (!this.insertItem(itemStack2, 10, 46, true)) {
					return null;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (invSlot >= 10 && invSlot < 37) {
				if (!this.insertItem(itemStack2, 37, 46, false)) {
					return null;
				}
			} else if (invSlot >= 37 && invSlot < 46) {
				if (!this.insertItem(itemStack2, 10, 37, false)) {
					return null;
				}
			} else if (!this.insertItem(itemStack2, 10, 46, false)) {
				return null;
			}

			if (itemStack2.count == 0) {
				slot.setStack(null);
			} else {
				slot.markDirty();
			}

			if (itemStack2.count == itemStack.count) {
				return null;
			}

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.resultInv && super.canInsertIntoSlot(stack, slot);
	}
}
