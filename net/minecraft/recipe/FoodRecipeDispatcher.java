package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

public class FoodRecipeDispatcher {
	public void register(RecipeDispatcher recipes) {
		recipes.registerShapelessRecipe(new ItemStack(Items.MUSHROOM_STEW), Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Items.BOWL);
		recipes.registerShapedRecipe(new ItemStack(Items.COOKIE, 8), "#X#", 'X', new ItemStack(Items.DYE, 1, DyeColor.BROWN.getSwappedId()), '#', Items.WHEAT);
		recipes.registerShapedRecipe(
			new ItemStack(Items.RABBIT_STEW),
			" R ",
			"CPM",
			" B ",
			'R',
			new ItemStack(Items.COOKED_RABBIT),
			'C',
			Items.CARROT,
			'P',
			Items.BAKED_POTATO,
			'M',
			Blocks.BROWN_MUSHROOM,
			'B',
			Items.BOWL
		);
		recipes.registerShapedRecipe(
			new ItemStack(Items.RABBIT_STEW),
			" R ",
			"CPD",
			" B ",
			'R',
			new ItemStack(Items.COOKED_RABBIT),
			'C',
			Items.CARROT,
			'P',
			Items.BAKED_POTATO,
			'D',
			Blocks.RED_MUSHROOM,
			'B',
			Items.BOWL
		);
		recipes.registerShapedRecipe(new ItemStack(Blocks.MELON_BLOCK), "MMM", "MMM", "MMM", 'M', Items.MELON);
		recipes.registerShapedRecipe(new ItemStack(Items.BEETROOT_SOUP), "OOO", "OOO", " B ", 'O', Items.BEETROOT, 'B', Items.BOWL);
		recipes.registerShapedRecipe(new ItemStack(Items.MELON_SEEDS), "M", 'M', Items.MELON);
		recipes.registerShapedRecipe(new ItemStack(Items.PUMPKIN_SEEDS, 4), "M", 'M', Blocks.PUMPKIN);
		recipes.registerShapelessRecipe(new ItemStack(Items.PUMPKIN_PIE), Blocks.PUMPKIN, Items.SUGAR, Items.EGG);
		recipes.registerShapelessRecipe(new ItemStack(Items.FERMENTED_SPIDER_EYE), Items.SPIDER_EYE, Blocks.BROWN_MUSHROOM, Items.SUGAR);
		recipes.registerShapelessRecipe(new ItemStack(Items.BLAZE_POWDER, 2), Items.BLAZE_ROD);
		recipes.registerShapelessRecipe(new ItemStack(Items.MAGMA_CREAM), Items.BLAZE_POWDER, Items.SLIME_BALL);
	}
}
