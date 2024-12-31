package net.minecraft;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;

public class class_3355 {
	protected final BitSet field_16469 = new BitSet();
	protected final BitSet field_16470 = new BitSet();
	protected boolean bookOpen;
	protected boolean filterActive;

	public void method_14984(class_3355 arg) {
		this.field_16469.clear();
		this.field_16470.clear();
		this.field_16469.or(arg.field_16469);
		this.field_16470.or(arg.field_16470);
	}

	public void method_14983(RecipeType recipeType) {
		if (!recipeType.method_14251()) {
			this.field_16469.set(method_14990(recipeType));
		}
	}

	public boolean method_14987(@Nullable RecipeType recipeType) {
		return this.field_16469.get(method_14990(recipeType));
	}

	public void method_14989(RecipeType recipeType) {
		int i = method_14990(recipeType);
		this.field_16469.clear(i);
		this.field_16470.clear(i);
	}

	protected static int method_14990(@Nullable RecipeType recipeType) {
		return RecipeDispatcher.REGISTRY.getRawId(recipeType);
	}

	public boolean method_14991(RecipeType recipeType) {
		return this.field_16470.get(method_14990(recipeType));
	}

	public void method_14992(RecipeType recipeType) {
		this.field_16470.clear(method_14990(recipeType));
	}

	public void method_14993(RecipeType recipeType) {
		this.field_16470.set(method_14990(recipeType));
	}

	public boolean method_14982() {
		return this.bookOpen;
	}

	public void method_14985(boolean bl) {
		this.bookOpen = bl;
	}

	public boolean method_14986() {
		return this.filterActive;
	}

	public void method_14988(boolean bl) {
		this.filterActive = bl;
	}
}
