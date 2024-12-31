package net.minecraft.client.toast;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

public class RecipeToast implements Toast {
	private final List<Recipe<?>> recipes = Lists.newArrayList();
	private long startTime;
	private boolean justUpdated;

	public RecipeToast(Recipe<?> recipe) {
		this.recipes.add(recipe);
	}

	@Override
	public Toast.Visibility draw(ToastManager toastManager, long l) {
		if (this.justUpdated) {
			this.startTime = l;
			this.justUpdated = false;
		}

		if (this.recipes.isEmpty()) {
			return Toast.Visibility.field_2209;
		} else {
			toastManager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
			RenderSystem.color3f(1.0F, 1.0F, 1.0F);
			toastManager.blit(0, 0, 0, 32, 160, 32);
			toastManager.getGame().textRenderer.draw(I18n.translate("recipe.toast.title"), 30.0F, 7.0F, -11534256);
			toastManager.getGame().textRenderer.draw(I18n.translate("recipe.toast.description"), 30.0F, 18.0F, -16777216);
			Recipe<?> recipe = (Recipe<?>)this.recipes.get((int)(l / (5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
			ItemStack itemStack = recipe.getRecipeKindIcon();
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.6F, 0.6F, 1.0F);
			toastManager.getGame().getItemRenderer().renderGuiItem(null, itemStack, 3, 3);
			RenderSystem.popMatrix();
			toastManager.getGame().getItemRenderer().renderGuiItem(null, recipe.getOutput(), 8, 8);
			return l - this.startTime >= 5000L ? Toast.Visibility.field_2209 : Toast.Visibility.field_2210;
		}
	}

	public void addRecipe(Recipe<?> recipe) {
		if (this.recipes.add(recipe)) {
			this.justUpdated = true;
		}
	}

	public static void show(ToastManager toastManager, Recipe<?> recipe) {
		RecipeToast recipeToast = toastManager.getToast(RecipeToast.class, field_2208);
		if (recipeToast == null) {
			toastManager.add(new RecipeToast(recipe));
		} else {
			recipeToast.addRecipe(recipe);
		}
	}
}
