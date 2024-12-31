package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import net.minecraft.recipe.Recipe;

public interface RecipeDisplayListener {
	void onRecipesDisplayed(List<Recipe<?>> list);
}
