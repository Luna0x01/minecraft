package net.minecraft.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StonecuttingRecipe extends CuttingRecipe {
	public StonecuttingRecipe(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack) {
		super(RecipeType.field_17641, RecipeSerializer.field_17640, identifier, string, ingredient, itemStack);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.input.test(inventory.getInvStack(0));
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.field_16335);
	}
}
