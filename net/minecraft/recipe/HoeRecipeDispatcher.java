package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HoeRecipeDispatcher {
	private String[][] pattern = new String[][]{{"XXX", " # ", " # "}, {"X", "#", "#"}, {"XX", "X#", " #"}, {"XX", " #", " #"}};
	private Object[][] items = new Object[][]{
		{Blocks.PLANKS, Blocks.COBBLESTONE, Items.IRON_INGOT, Items.DIAMOND, Items.GOLD_INGOT},
		{Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE, Items.GOLDEN_PICKAXE},
		{Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL, Items.GOLDEN_SHOVEL},
		{Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.GOLDEN_AXE},
		{Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.DIAMOND_HOE, Items.GOLDEN_HOE}
	};

	public void register(RecipeDispatcher recipes) {
		for (int i = 0; i < this.items[0].length; i++) {
			Object object = this.items[0][i];

			for (int j = 0; j < this.items.length - 1; j++) {
				Item item = (Item)this.items[j + 1][i];
				recipes.registerShapedRecipe(new ItemStack(item), this.pattern[j], '#', Items.STICK, 'X', object);
			}
		}

		recipes.registerShapedRecipe(new ItemStack(Items.SHEARS), " #", "# ", '#', Items.IRON_INGOT);
	}
}
