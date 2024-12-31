package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.MathHelper;

public class class_3278 {
	private RecipeType field_16023;
	private final List<class_3278.class_3279> field_16024 = Lists.newArrayList();
	private float field_16025;

	public void method_14551() {
		this.field_16023 = null;
		this.field_16024.clear();
		this.field_16025 = 0.0F;
	}

	public void method_14553(Ingredient ingredient, int i, int j) {
		this.field_16024.add(new class_3278.class_3279(ingredient, i, j));
	}

	public class_3278.class_3279 method_14552(int i) {
		return (class_3278.class_3279)this.field_16024.get(i);
	}

	public int method_14557() {
		return this.field_16024.size();
	}

	@Nullable
	public RecipeType method_14558() {
		return this.field_16023;
	}

	public void method_14554(RecipeType recipeType) {
		this.field_16023 = recipeType;
	}

	public void method_14555(MinecraftClient minecraftClient, int i, int j, boolean bl, float f) {
		if (!Screen.hasControlDown()) {
			this.field_16025 += f;
		}

		DiffuseLighting.enable();
		GlStateManager.disableLighting();

		for (int k = 0; k < this.field_16024.size(); k++) {
			class_3278.class_3279 lv = (class_3278.class_3279)this.field_16024.get(k);
			int l = lv.method_14559() + i;
			int m = lv.method_14560() + j;
			if (k == 0 && bl) {
				DrawableHelper.fill(l - 4, m - 4, l + 20, m + 20, 822018048);
			} else {
				DrawableHelper.fill(l, m, l + 16, m + 16, 822018048);
			}

			GlStateManager.disableLighting();
			ItemStack itemStack = lv.method_14561();
			ItemRenderer itemRenderer = minecraftClient.getItemRenderer();
			itemRenderer.method_10249(minecraftClient.player, itemStack, l, m);
			GlStateManager.depthFunc(516);
			DrawableHelper.fill(l, m, l + 16, m + 16, 822083583);
			GlStateManager.depthFunc(515);
			if (k == 0) {
				itemRenderer.renderGuiItemOverlay(minecraftClient.textRenderer, itemStack, l, m);
			}

			GlStateManager.enableLighting();
		}

		DiffuseLighting.disable();
	}

	public class class_3279 {
		private final Ingredient field_16027;
		private final int field_16028;
		private final int field_16029;

		public class_3279(Ingredient ingredient, int i, int j) {
			this.field_16027 = ingredient;
			this.field_16028 = i;
			this.field_16029 = j;
		}

		public int method_14559() {
			return this.field_16028;
		}

		public int method_14560() {
			return this.field_16029;
		}

		public ItemStack method_14561() {
			ItemStack[] itemStacks = this.field_16027.method_14244();
			return itemStacks[MathHelper.floor(class_3278.this.field_16025 / 30.0F) % itemStacks.length];
		}
	}
}
