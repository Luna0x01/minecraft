package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
	public CampfireCookingRecipe(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		super(RecipeType.CAMPFIRE_COOKING, identifier, string, ingredient, itemStack, f, i);
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.field_17350);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.CAMPFIRE_COOKING;
	}
}
