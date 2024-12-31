package net.minecraft.screen;

import net.minecraft.class_3175;
import net.minecraft.class_3536;
import net.minecraft.class_3538;
import net.minecraft.class_3584;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.FurnaceFuelSlot;
import net.minecraft.inventory.slot.FurnaceOutputSlot;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

public class FurnaceScreenHandler extends class_3536 {
	private final Inventory inventory;
	private final World field_17130;
	private int cookTime;
	private int totalCookTime;
	private int fuelTime;
	private int totalFuelTime;

	public FurnaceScreenHandler(PlayerInventory playerInventory, Inventory inventory) {
		this.inventory = inventory;
		this.field_17130 = playerInventory.player.world;
		this.addSlot(new Slot(inventory, 0, 56, 17));
		this.addSlot(new FurnaceFuelSlot(inventory, 1, 56, 53));
		this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 2, 116, 35));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; k++) {
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
		}
	}

	@Override
	public void addListener(ScreenHandlerListener listener) {
		super.addListener(listener);
		listener.onScreenHandlerInventoryUpdate(this, this.inventory);
	}

	@Override
	public void method_15978(class_3175 arg) {
		if (this.inventory instanceof class_3538) {
			((class_3538)this.inventory).method_15987(arg);
		}
	}

	@Override
	public void method_15980() {
		this.inventory.clear();
	}

	@Override
	public boolean method_15979(RecipeType recipeType) {
		return recipeType.method_3500(this.inventory, this.field_17130);
	}

	@Override
	public int method_15981() {
		return 2;
	}

	@Override
	public int method_15982() {
		return 1;
	}

	@Override
	public int method_15983() {
		return 1;
	}

	@Override
	public int method_15984() {
		return 3;
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();

		for (ScreenHandlerListener screenHandlerListener : this.listeners) {
			if (this.cookTime != this.inventory.getProperty(2)) {
				screenHandlerListener.onScreenHandlerPropertyUpdate(this, 2, this.inventory.getProperty(2));
			}

			if (this.fuelTime != this.inventory.getProperty(0)) {
				screenHandlerListener.onScreenHandlerPropertyUpdate(this, 0, this.inventory.getProperty(0));
			}

			if (this.totalFuelTime != this.inventory.getProperty(1)) {
				screenHandlerListener.onScreenHandlerPropertyUpdate(this, 1, this.inventory.getProperty(1));
			}

			if (this.totalCookTime != this.inventory.getProperty(3)) {
				screenHandlerListener.onScreenHandlerPropertyUpdate(this, 3, this.inventory.getProperty(3));
			}
		}

		this.cookTime = this.inventory.getProperty(2);
		this.fuelTime = this.inventory.getProperty(0);
		this.totalFuelTime = this.inventory.getProperty(1);
		this.totalCookTime = this.inventory.getProperty(3);
	}

	@Override
	public void setProperty(int id, int value) {
		this.inventory.setProperty(id, value);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUseInv(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (invSlot != 1 && invSlot != 0) {
				if (this.method_15976(itemStack2)) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (FurnaceBlockEntity.isFuel(itemStack2)) {
					if (!this.insertItem(itemStack2, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (invSlot >= 3 && invSlot < 30) {
					if (!this.insertItem(itemStack2, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (invSlot >= 30 && invSlot < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
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

			slot.method_3298(player, itemStack2);
		}

		return itemStack;
	}

	private boolean method_15976(ItemStack itemStack) {
		for (RecipeType recipeType : this.field_17130.method_16313().method_16208()) {
			if (recipeType instanceof class_3584 && recipeType.method_14252().get(0).test(itemStack)) {
				return true;
			}
		}

		return false;
	}
}
