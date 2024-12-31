package net.minecraft.recipe;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

public class NineGoldRecipeDispatcher {
	private final Object[][] recipe = new Object[][]{
		{Blocks.GOLD_BLOCK, new ItemStack(Items.GOLD_INGOT, 9)},
		{Blocks.IRON_BLOCK, new ItemStack(Items.IRON_INGOT, 9)},
		{Blocks.DIAMOND_BLOCK, new ItemStack(Items.DIAMOND, 9)},
		{Blocks.EMERALD_BLOCK, new ItemStack(Items.EMERALD, 9)},
		{Blocks.LAPIS_LAZULI_BLOCK, new ItemStack(Items.DYE, 9, DyeColor.BLUE.getSwappedId())},
		{Blocks.REDSTONE_BLOCK, new ItemStack(Items.REDSTONE, 9)},
		{Blocks.COAL_BLOCK, new ItemStack(Items.COAL, 9, 0)},
		{Blocks.HAY_BALE, new ItemStack(Items.WHEAT, 9)},
		{Blocks.SLIME_BLOCK, new ItemStack(Items.SLIME_BALL, 9)}
	};

	public void register(RecipeDispatcher recipes) {
		for (Object[] objects2 : this.recipe) {
			Block block = (Block)objects2[0];
			ItemStack itemStack = (ItemStack)objects2[1];
			recipes.registerShapedRecipe(new ItemStack(block), "###", "###", "###", '#', itemStack);
			recipes.registerShapedRecipe(itemStack, "#", '#', block);
		}

		recipes.registerShapedRecipe(new ItemStack(Items.GOLD_INGOT), "###", "###", "###", '#', Items.GOLD_NUGGET);
		recipes.registerShapedRecipe(new ItemStack(Items.GOLD_NUGGET, 9), "#", '#', Items.GOLD_INGOT);
	}
}
