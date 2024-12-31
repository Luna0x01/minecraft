package net.minecraft.recipe.book;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.container.SmokerContainer;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class RecipeBook {
	protected final Set<Identifier> recipes = Sets.newHashSet();
	protected final Set<Identifier> toBeDisplayed = Sets.newHashSet();
	protected boolean guiOpen;
	protected boolean filteringCraftable;
	protected boolean furnaceGuiOpen;
	protected boolean furnaceFilteringCraftable;
	protected boolean blastFurnaceGuiOpen;
	protected boolean blastFurnaceFilteringCraftable;
	protected boolean smokerGuiOpen;
	protected boolean smokerFilteringCraftable;

	public void copyFrom(RecipeBook recipeBook) {
		this.recipes.clear();
		this.toBeDisplayed.clear();
		this.recipes.addAll(recipeBook.recipes);
		this.toBeDisplayed.addAll(recipeBook.toBeDisplayed);
	}

	public void add(Recipe<?> recipe) {
		if (!recipe.isIgnoredInRecipeBook()) {
			this.add(recipe.getId());
		}
	}

	protected void add(Identifier identifier) {
		this.recipes.add(identifier);
	}

	public boolean contains(@Nullable Recipe<?> recipe) {
		return recipe == null ? false : this.recipes.contains(recipe.getId());
	}

	public void remove(Recipe<?> recipe) {
		this.remove(recipe.getId());
	}

	protected void remove(Identifier identifier) {
		this.recipes.remove(identifier);
		this.toBeDisplayed.remove(identifier);
	}

	public boolean shouldDisplay(Recipe<?> recipe) {
		return this.toBeDisplayed.contains(recipe.getId());
	}

	public void onRecipeDisplayed(Recipe<?> recipe) {
		this.toBeDisplayed.remove(recipe.getId());
	}

	public void display(Recipe<?> recipe) {
		this.display(recipe.getId());
	}

	protected void display(Identifier identifier) {
		this.toBeDisplayed.add(identifier);
	}

	public boolean isGuiOpen() {
		return this.guiOpen;
	}

	public void setGuiOpen(boolean bl) {
		this.guiOpen = bl;
	}

	public boolean isFilteringCraftable(CraftingContainer<?> craftingContainer) {
		if (craftingContainer instanceof FurnaceContainer) {
			return this.furnaceFilteringCraftable;
		} else if (craftingContainer instanceof BlastFurnaceContainer) {
			return this.blastFurnaceFilteringCraftable;
		} else {
			return craftingContainer instanceof SmokerContainer ? this.smokerFilteringCraftable : this.filteringCraftable;
		}
	}

	public boolean isFilteringCraftable() {
		return this.filteringCraftable;
	}

	public void setFilteringCraftable(boolean bl) {
		this.filteringCraftable = bl;
	}

	public boolean isFurnaceGuiOpen() {
		return this.furnaceGuiOpen;
	}

	public void setFurnaceGuiOpen(boolean bl) {
		this.furnaceGuiOpen = bl;
	}

	public boolean isFurnaceFilteringCraftable() {
		return this.furnaceFilteringCraftable;
	}

	public void setFurnaceFilteringCraftable(boolean bl) {
		this.furnaceFilteringCraftable = bl;
	}

	public boolean isBlastFurnaceGuiOpen() {
		return this.blastFurnaceGuiOpen;
	}

	public void setBlastFurnaceGuiOpen(boolean bl) {
		this.blastFurnaceGuiOpen = bl;
	}

	public boolean isBlastFurnaceFilteringCraftable() {
		return this.blastFurnaceFilteringCraftable;
	}

	public void setBlastFurnaceFilteringCraftable(boolean bl) {
		this.blastFurnaceFilteringCraftable = bl;
	}

	public boolean isSmokerGuiOpen() {
		return this.smokerGuiOpen;
	}

	public void setSmokerGuiOpen(boolean bl) {
		this.smokerGuiOpen = bl;
	}

	public boolean isSmokerFilteringCraftable() {
		return this.smokerFilteringCraftable;
	}

	public void setSmokerFilteringCraftable(boolean bl) {
		this.smokerFilteringCraftable = bl;
	}
}
