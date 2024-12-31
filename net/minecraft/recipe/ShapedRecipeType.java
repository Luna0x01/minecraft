package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShapedRecipeType implements RecipeType {
	private final int width;
	private final int height;
	private final ItemStack[] ingredients;
	private final ItemStack result;
	private boolean copyIngredientsNbt;

	public ShapedRecipeType(int i, int j, ItemStack[] itemStacks, ItemStack itemStack) {
		this.width = i;
		this.height = j;
		this.ingredients = itemStacks;

		for (int k = 0; k < this.ingredients.length; k++) {
			if (this.ingredients[k] == null) {
				this.ingredients[k] = ItemStack.EMPTY;
			}
		}

		this.result = itemStack;
	}

	@Override
	public ItemStack getOutput() {
		return this.result;
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
	public boolean matches(CraftingInventory inventory, World world) {
		for (int i = 0; i <= 3 - this.width; i++) {
			for (int j = 0; j <= 3 - this.height; j++) {
				if (this.method_3503(inventory, i, j, true)) {
					return true;
				}

				if (this.method_3503(inventory, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean method_3503(CraftingInventory inventory, int width, int height, boolean bl) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int k = i - width;
				int l = j - height;
				ItemStack itemStack = ItemStack.EMPTY;
				if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
					if (bl) {
						itemStack = this.ingredients[this.width - k - 1 + l * this.width];
					} else {
						itemStack = this.ingredients[k + l * this.width];
					}
				}

				ItemStack itemStack2 = inventory.getStackAt(i, j);
				if (!itemStack2.isEmpty() || !itemStack.isEmpty()) {
					if (itemStack2.isEmpty() != itemStack.isEmpty()) {
						return false;
					}

					if (itemStack.getItem() != itemStack2.getItem()) {
						return false;
					}

					if (itemStack.getData() != 32767 && itemStack.getData() != itemStack2.getData()) {
						return false;
					}
				}
			}
		}

		return true;
	}

	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		ItemStack itemStack = this.getOutput().copy();
		if (this.copyIngredientsNbt) {
			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack2 = inventory.getInvStack(i);
				if (!itemStack2.isEmpty() && itemStack2.hasNbt()) {
					itemStack.setNbt(itemStack2.getNbt().copy());
				}
			}
		}

		return itemStack;
	}

	@Override
	public int getSize() {
		return this.width * this.height;
	}
}
