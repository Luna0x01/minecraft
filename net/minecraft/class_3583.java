package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class class_3583 extends class_3571 {
	public class_3583(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			int i = 0;
			int j = 0;

			for (int k = 0; k < inventory.getInvSize(); k++) {
				ItemStack itemStack = inventory.getInvStack(k);
				if (!itemStack.isEmpty()) {
					if (Block.getBlockFromItem(itemStack.getItem()) instanceof ShulkerBoxBlock) {
						i++;
					} else {
						if (!(itemStack.getItem() instanceof DyeItem)) {
							return false;
						}

						j++;
					}

					if (j > 1 || i > 1) {
						return false;
					}
				}
			}

			return i == 1 && j == 1;
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		ItemStack itemStack = ItemStack.EMPTY;
		DyeItem dyeItem = (DyeItem)Items.BONE_MEAL;

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			if (!itemStack2.isEmpty()) {
				Item item = itemStack2.getItem();
				if (Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) {
					itemStack = itemStack2;
				} else if (item instanceof DyeItem) {
					dyeItem = (DyeItem)item;
				}
			}
		}

		ItemStack itemStack3 = ShulkerBoxBlock.stackOf(dyeItem.method_16047());
		if (itemStack.hasNbt()) {
			itemStack3.setNbt(itemStack.getNbt().copy());
		}

		return itemStack3;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17461;
	}
}
