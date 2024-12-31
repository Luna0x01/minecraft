package net.minecraft.recipe;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShulkerBoxColoringRecipe extends SpecialCraftingRecipe {
	public ShulkerBoxColoringRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean method_17734(CraftingInventory craftingInventory, World world) {
		int i = 0;
		int j = 0;

		for (int k = 0; k < craftingInventory.getInvSize(); k++) {
			ItemStack itemStack = craftingInventory.getInvStack(k);
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

	public ItemStack method_17733(CraftingInventory craftingInventory) {
		ItemStack itemStack = ItemStack.EMPTY;
		DyeItem dyeItem = (DyeItem)Items.field_8446;

		for (int i = 0; i < craftingInventory.getInvSize(); i++) {
			ItemStack itemStack2 = craftingInventory.getInvStack(i);
			if (!itemStack2.isEmpty()) {
				Item item = itemStack2.getItem();
				if (Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) {
					itemStack = itemStack2;
				} else if (item instanceof DyeItem) {
					dyeItem = (DyeItem)item;
				}
			}
		}

		ItemStack itemStack3 = ShulkerBoxBlock.getItemStack(dyeItem.getColor());
		if (itemStack.hasTag()) {
			itemStack3.setTag(itemStack.getTag().method_10553());
		}

		return itemStack3;
	}

	@Override
	public boolean fits(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SHULKER_BOX;
	}
}
