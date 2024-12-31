package net.minecraft.recipe;

import javax.annotation.Nullable;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.world.World;

class TippedArrowRecipeType implements RecipeType {
	private static final ItemStack[] field_12385 = new ItemStack[9];

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		if (inventory.getWidth() == 3 && inventory.getHeight() == 3) {
			for (int i = 0; i < inventory.getWidth(); i++) {
				for (int j = 0; j < inventory.getHeight(); j++) {
					ItemStack itemStack = inventory.getStackAt(i, j);
					if (itemStack == null) {
						return false;
					}

					Item item = itemStack.getItem();
					if (i == 1 && j == 1) {
						if (item != Items.LINGERING_POTION) {
							return false;
						}
					} else if (item != Items.ARROW) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		ItemStack itemStack = inventory.getStackAt(1, 1);
		if (itemStack != null && itemStack.getItem() == Items.LINGERING_POTION) {
			ItemStack itemStack2 = new ItemStack(Items.TIPPED_ARROW, 8);
			PotionUtil.setPotion(itemStack2, PotionUtil.getPotion(itemStack));
			PotionUtil.setCustomPotionEffects(itemStack2, PotionUtil.getCustomPotionEffects(itemStack));
			return itemStack2;
		} else {
			return null;
		}
	}

	@Override
	public int getSize() {
		return 9;
	}

	@Nullable
	@Override
	public ItemStack getOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainders(CraftingInventory inventory) {
		return field_12385;
	}
}
