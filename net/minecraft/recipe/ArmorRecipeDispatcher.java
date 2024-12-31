package net.minecraft.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArmorRecipeDispatcher {
	private String[][] pattern = new String[][]{{"XXX", "X X"}, {"X X", "XXX", "XXX"}, {"XXX", "X X", "X X"}, {"X X", "X X"}};
	private Item[][] items = new Item[][]{
		{Items.LEATHER, Items.IRON_INGOT, Items.DIAMOND, Items.GOLD_INGOT},
		{Items.LEATHER_HELMET, Items.IRON_HELMET, Items.DIAMOND_HELMET, Items.GOLDEN_HELMET},
		{Items.LEATHER_CHESTPLATE, Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.GOLDEN_CHESTPLATE},
		{Items.LEATHER_LEGGINGS, Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.GOLDEN_LEGGINGS},
		{Items.LEATHER_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS, Items.GOLDEN_BOOTS}
	};

	public void register(RecipeDispatcher recipes) {
		for (int i = 0; i < this.items[0].length; i++) {
			Item item = this.items[0][i];

			for (int j = 0; j < this.items.length - 1; j++) {
				Item item2 = this.items[j + 1][i];
				recipes.registerShapedRecipe(new ItemStack(item2), this.pattern[j], 'X', item);
			}
		}
	}
}
