package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBook;

public class RecipeResultCollection {
	private final List<Recipe<?>> recipes = Lists.newArrayList();
	private final Set<Recipe<?>> craftableRecipes = Sets.newHashSet();
	private final Set<Recipe<?>> fittingRecipes = Sets.newHashSet();
	private final Set<Recipe<?>> unlockedRecipes = Sets.newHashSet();
	private boolean singleOutput = true;

	public boolean isInitialized() {
		return !this.unlockedRecipes.isEmpty();
	}

	public void initialize(RecipeBook recipeBook) {
		for (Recipe<?> recipe : this.recipes) {
			if (recipeBook.contains(recipe)) {
				this.unlockedRecipes.add(recipe);
			}
		}
	}

	public void computeCraftables(RecipeFinder recipeFinder, int i, int j, RecipeBook recipeBook) {
		for (int k = 0; k < this.recipes.size(); k++) {
			Recipe<?> recipe = (Recipe<?>)this.recipes.get(k);
			boolean bl = recipe.fits(i, j) && recipeBook.contains(recipe);
			if (bl) {
				this.fittingRecipes.add(recipe);
			} else {
				this.fittingRecipes.remove(recipe);
			}

			if (bl && recipeFinder.findRecipe(recipe, null)) {
				this.craftableRecipes.add(recipe);
			} else {
				this.craftableRecipes.remove(recipe);
			}
		}
	}

	public boolean isCraftable(Recipe<?> recipe) {
		return this.craftableRecipes.contains(recipe);
	}

	public boolean hasCraftableRecipes() {
		return !this.craftableRecipes.isEmpty();
	}

	public boolean hasFittingRecipes() {
		return !this.fittingRecipes.isEmpty();
	}

	public List<Recipe<?>> getAllRecipes() {
		return this.recipes;
	}

	public List<Recipe<?>> getResults(boolean bl) {
		List<Recipe<?>> list = Lists.newArrayList();
		Set<Recipe<?>> set = bl ? this.craftableRecipes : this.fittingRecipes;

		for (Recipe<?> recipe : this.recipes) {
			if (set.contains(recipe)) {
				list.add(recipe);
			}
		}

		return list;
	}

	public List<Recipe<?>> getRecipes(boolean bl) {
		List<Recipe<?>> list = Lists.newArrayList();

		for (Recipe<?> recipe : this.recipes) {
			if (this.fittingRecipes.contains(recipe) && this.craftableRecipes.contains(recipe) == bl) {
				list.add(recipe);
			}
		}

		return list;
	}

	public void addRecipe(Recipe<?> recipe) {
		this.recipes.add(recipe);
		if (this.singleOutput) {
			ItemStack itemStack = ((Recipe)this.recipes.get(0)).getOutput();
			ItemStack itemStack2 = recipe.getOutput();
			this.singleOutput = ItemStack.areItemsEqualIgnoreDamage(itemStack, itemStack2) && ItemStack.areTagsEqual(itemStack, itemStack2);
		}
	}

	public boolean hasSingleOutput() {
		return this.singleOutput;
	}
}
