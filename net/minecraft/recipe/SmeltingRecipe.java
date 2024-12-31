package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class SmeltingRecipe extends AbstractCookingRecipe {
	public SmeltingRecipe(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		super(RecipeType.SMELTING, identifier, string, ingredient, itemStack, f, i);
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.field_10181);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.SMELTING;
	}
}
