package net.minecraft.recipe;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShulkerBoxRecipeDispatcher {
	public static class ColoringRecipeType implements RecipeType {
		@Override
		public boolean matches(CraftingInventory inventory, World world) {
			int i = 0;
			int j = 0;

			for (int k = 0; k < inventory.getInvSize(); k++) {
				ItemStack itemStack = inventory.getInvStack(k);
				if (!itemStack.isEmpty()) {
					if (Block.getBlockFromItem(itemStack.getItem()) instanceof ShulkerBoxBlock) {
						i++;
					} else {
						if (itemStack.getItem() != Items.DYE) {
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

		@Override
		public ItemStack getResult(CraftingInventory inventory) {
			ItemStack itemStack = ItemStack.EMPTY;
			ItemStack itemStack2 = ItemStack.EMPTY;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack3 = inventory.getInvStack(i);
				if (!itemStack3.isEmpty()) {
					if (Block.getBlockFromItem(itemStack3.getItem()) instanceof ShulkerBoxBlock) {
						itemStack = itemStack3;
					} else if (itemStack3.getItem() == Items.DYE) {
						itemStack2 = itemStack3;
					}
				}
			}

			ItemStack itemStack4 = ShulkerBoxBlock.stackOf(DyeColor.getById(itemStack2.getData()));
			if (itemStack.hasNbt()) {
				itemStack4.setNbt(itemStack.getNbt().copy());
			}

			return itemStack4;
		}

		@Override
		public ItemStack getOutput() {
			return ItemStack.EMPTY;
		}

		@Override
		public DefaultedList<ItemStack> method_13670(CraftingInventory craftingInventory) {
			DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.getInvSize(), ItemStack.EMPTY);

			for (int i = 0; i < defaultedList.size(); i++) {
				ItemStack itemStack = craftingInventory.getInvStack(i);
				if (itemStack.getItem().isFood()) {
					defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
				}
			}

			return defaultedList;
		}

		@Override
		public boolean method_14251() {
			return true;
		}

		@Override
		public boolean method_14250(int i, int j) {
			return i * j >= 2;
		}
	}
}
