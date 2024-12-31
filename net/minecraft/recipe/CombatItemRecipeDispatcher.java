package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CombatItemRecipeDispatcher {
	private final String[][] pattern = new String[][]{{"X", "X", "#"}};
	private final Object[][] items = new Object[][]{
		{Blocks.PLANKS, Blocks.COBBLESTONE, Items.IRON_INGOT, Items.DIAMOND, Items.GOLD_INGOT},
		{Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.GOLDEN_SWORD}
	};

	public void register(RecipeDispatcher recipes) {
		for (int i = 0; i < this.items[0].length; i++) {
			Object object = this.items[0][i];

			for (int j = 0; j < this.items.length - 1; j++) {
				Item item = (Item)this.items[j + 1][i];
				recipes.registerShapedRecipe(new ItemStack(item), this.pattern[j], '#', Items.STICK, 'X', object);
			}
		}

		recipes.registerShapedRecipe(new ItemStack(Items.BOW, 1), " #X", "# X", " #X", 'X', Items.STRING, '#', Items.STICK);
		recipes.registerShapedRecipe(new ItemStack(Items.ARROW, 4), "X", "#", "Y", 'Y', Items.FEATHER, 'X', Items.FLINT, '#', Items.STICK);
		recipes.registerShapedRecipe(new ItemStack(Items.SPECTRAL_ARROW, 2), " # ", "#X#", " # ", 'X', Items.ARROW, '#', Items.GLOWSTONE_DUST);
	}
}
