package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class SmokingRecipe extends AbstractCookingRecipe {
	public SmokingRecipe(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		super(RecipeType.SMOKING, identifier, string, ingredient, itemStack, f, i);
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.field_16334);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SMOKING;
	}
}
