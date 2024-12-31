package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.RecipeBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;

public class class_4172 extends RecipeBookScreen {
	private Iterator<Item> field_20459;
	private Set<Item> field_20460;
	private Slot field_20461;
	private Item field_20462;
	private float field_20463;

	@Override
	protected boolean method_18797() {
		boolean bl = !this.field_20451.method_21406();
		this.field_20451.method_21408(bl);
		return bl;
	}

	@Override
	public boolean method_14590() {
		return this.field_20451.method_21402();
	}

	@Override
	protected void method_14584(boolean bl) {
		this.field_20451.method_21405(bl);
		if (!bl) {
			this.field_16055.method_14611();
		}

		this.method_14601();
	}

	@Override
	protected void method_18791() {
		this.field_16049.method_14477(152, 182, 28, 18, TEXTURE);
	}

	@Override
	protected String method_18796() {
		return I18n.translate(this.field_16049.method_14479() ? "gui.recipebook.toggleRecipes.smeltable" : "gui.recipebook.toggleRecipes.all");
	}

	@Override
	public void method_14579(@Nullable Slot slot) {
		super.method_14579(slot);
		if (slot != null && slot.id < this.field_20450.method_15984()) {
			this.field_20461 = null;
		}
	}

	@Override
	public void method_14580(RecipeType recipeType, List<Slot> list) {
		ItemStack itemStack = recipeType.getOutput();
		this.field_16046.method_14554(recipeType);
		this.field_16046.method_14553(Ingredient.method_14248(itemStack), ((Slot)list.get(2)).x, ((Slot)list.get(2)).y);
		DefaultedList<Ingredient> defaultedList = recipeType.method_14252();
		this.field_20461 = (Slot)list.get(1);
		if (this.field_20460 == null) {
			this.field_20460 = FurnaceBlockEntity.method_16817().keySet();
		}

		this.field_20459 = this.field_20460.iterator();
		this.field_20462 = null;
		Iterator<Ingredient> iterator = defaultedList.iterator();

		for (int i = 0; i < 2; i++) {
			if (!iterator.hasNext()) {
				return;
			}

			Ingredient ingredient = (Ingredient)iterator.next();
			if (!ingredient.method_16196()) {
				Slot slot = (Slot)list.get(i);
				this.field_16046.method_14553(ingredient, slot.x, slot.y);
			}
		}
	}

	@Override
	public void method_14578(int i, int j, boolean bl, float f) {
		super.method_14578(i, j, bl, f);
		if (this.field_20461 != null) {
			if (!Screen.hasControlDown()) {
				this.field_20463 += f;
			}

			DiffuseLighting.enable();
			GlStateManager.disableLighting();
			int k = this.field_20461.x + i;
			int l = this.field_20461.y + j;
			DrawableHelper.fill(k, l, k + 16, l + 16, 822018048);
			this.client.getHeldItemRenderer().method_19374(this.client.player, this.method_18804().getDefaultStack(), k, l);
			GlStateManager.depthFunc(516);
			DrawableHelper.fill(k, l, k + 16, l + 16, 822083583);
			GlStateManager.depthFunc(515);
			GlStateManager.enableLighting();
			DiffuseLighting.disable();
		}
	}

	private Item method_18804() {
		if (this.field_20462 == null || this.field_20463 > 30.0F) {
			this.field_20463 = 0.0F;
			if (this.field_20459 == null || !this.field_20459.hasNext()) {
				if (this.field_20460 == null) {
					this.field_20460 = FurnaceBlockEntity.method_16817().keySet();
				}

				this.field_20459 = this.field_20460.iterator();
			}

			this.field_20462 = (Item)this.field_20459.next();
		}

		return this.field_20462;
	}
}
