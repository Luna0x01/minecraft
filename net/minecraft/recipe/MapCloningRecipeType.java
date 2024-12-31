package net.minecraft.recipe;

import net.minecraft.class_3571;
import net.minecraft.class_3578;
import net.minecraft.class_3579;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MapCloningRecipeType extends class_3571 {
	public MapCloningRecipeType(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			int i = 0;
			ItemStack itemStack = ItemStack.EMPTY;

			for (int j = 0; j < inventory.getInvSize(); j++) {
				ItemStack itemStack2 = inventory.getInvStack(j);
				if (!itemStack2.isEmpty()) {
					if (itemStack2.getItem() == Items.FILLED_MAP) {
						if (!itemStack.isEmpty()) {
							return false;
						}

						itemStack = itemStack2;
					} else {
						if (itemStack2.getItem() != Items.MAP) {
							return false;
						}

						i++;
					}
				}
			}

			return !itemStack.isEmpty() && i > 0;
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		int i = 0;
		ItemStack itemStack = ItemStack.EMPTY;

		for (int j = 0; j < inventory.getInvSize(); j++) {
			ItemStack itemStack2 = inventory.getInvStack(j);
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getItem() == Items.FILLED_MAP) {
					if (!itemStack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemStack = itemStack2;
				} else {
					if (itemStack2.getItem() != Items.MAP) {
						return ItemStack.EMPTY;
					}

					i++;
				}
			}
		}

		if (!itemStack.isEmpty() && i >= 1) {
			ItemStack itemStack3 = itemStack.copy();
			itemStack3.setCount(i + 1);
			return itemStack3;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i >= 3 && j >= 3;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17451;
	}
}
