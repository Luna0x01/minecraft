package net.minecraft;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public abstract class class_3571 implements RecipeType {
	private final Identifier field_17429;

	public class_3571(Identifier identifier) {
		this.field_17429 = identifier;
	}

	@Override
	public Identifier method_16202() {
		return this.field_17429;
	}

	@Override
	public boolean method_14251() {
		return true;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}
}
