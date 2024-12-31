package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShieldRecipeDispatcher {
	public void method_11444(RecipeDispatcher recipeDispatcher) {
		recipeDispatcher.registerShapedRecipe(new ItemStack(Items.SHIELD), "WoW", "WWW", " W ", 'W', Blocks.PLANKS, 'o', Items.IRON_INGOT);
		recipeDispatcher.addRecipeType(new ShieldRecipeDispatcher.DecorationRecipeType());
	}

	static class DecorationRecipeType implements RecipeType {
		private DecorationRecipeType() {
		}

		@Override
		public boolean matches(CraftingInventory inventory, World world) {
			ItemStack itemStack = ItemStack.EMPTY;
			ItemStack itemStack2 = ItemStack.EMPTY;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack3 = inventory.getInvStack(i);
				if (!itemStack3.isEmpty()) {
					if (itemStack3.getItem() == Items.BANNER) {
						if (!itemStack2.isEmpty()) {
							return false;
						}

						itemStack2 = itemStack3;
					} else {
						if (itemStack3.getItem() != Items.SHIELD) {
							return false;
						}

						if (!itemStack.isEmpty()) {
							return false;
						}

						if (itemStack3.getNbtCompound("BlockEntityTag") != null) {
							return false;
						}

						itemStack = itemStack3;
					}
				}
			}

			return !itemStack.isEmpty() && !itemStack2.isEmpty();
		}

		@Override
		public ItemStack getResult(CraftingInventory inventory) {
			ItemStack itemStack = ItemStack.EMPTY;
			ItemStack itemStack2 = ItemStack.EMPTY;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack3 = inventory.getInvStack(i);
				if (!itemStack3.isEmpty()) {
					if (itemStack3.getItem() == Items.BANNER) {
						itemStack = itemStack3;
					} else if (itemStack3.getItem() == Items.SHIELD) {
						itemStack2 = itemStack3.copy();
					}
				}
			}

			if (itemStack2.isEmpty()) {
				return itemStack2;
			} else {
				NbtCompound nbtCompound = itemStack.getNbtCompound("BlockEntityTag");
				NbtCompound nbtCompound2 = nbtCompound == null ? new NbtCompound() : nbtCompound.copy();
				nbtCompound2.putInt("Base", itemStack.getData() & 15);
				itemStack2.putSubNbt("BlockEntityTag", nbtCompound2);
				return itemStack2;
			}
		}

		@Override
		public int getSize() {
			return 2;
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
	}
}
