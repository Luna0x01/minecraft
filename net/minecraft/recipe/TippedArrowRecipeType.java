package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class TippedArrowRecipeType implements RecipeType {
	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		if (inventory.getWidth() == 3 && inventory.getHeight() == 3) {
			for (int i = 0; i < inventory.getWidth(); i++) {
				for (int j = 0; j < inventory.getHeight(); j++) {
					ItemStack itemStack = inventory.getStackAt(i, j);
					if (itemStack.isEmpty()) {
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

	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		ItemStack itemStack = inventory.getStackAt(1, 1);
		if (itemStack.getItem() != Items.LINGERING_POTION) {
			return ItemStack.EMPTY;
		} else {
			ItemStack itemStack2 = new ItemStack(Items.TIPPED_ARROW, 8);
			PotionUtil.setPotion(itemStack2, PotionUtil.getPotion(itemStack));
			PotionUtil.setCustomPotionEffects(itemStack2, PotionUtil.getCustomPotionEffects(itemStack));
			return itemStack2;
		}
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public DefaultedList<ItemStack> method_13670(CraftingInventory craftingInventory) {
		return DefaultedList.ofSize(craftingInventory.getInvSize(), ItemStack.EMPTY);
	}

	@Override
	public boolean method_14251() {
		return true;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i >= 2 && j >= 2;
	}
}
