package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class class_3280 extends DrawableHelper {
	private static final Identifier field_16030 = new Identifier("textures/gui/recipe_book.png");
	private final List<class_3280.class_3281> field_16031 = Lists.newArrayList();
	private boolean field_16032;
	private int field_16033;
	private int field_16034;
	private MinecraftClient field_16035;
	private class_3286 field_16036;
	private RecipeType field_16037;
	private float field_16038;

	public void method_14565(MinecraftClient minecraftClient, class_3286 arg, int i, int j, int k, int l, float f, class_3355 arg2) {
		this.field_16035 = minecraftClient;
		this.field_16036 = arg;
		boolean bl = arg2.method_14986();
		List<RecipeType> list = arg.method_14632(true);
		List<RecipeType> list2 = bl ? Collections.emptyList() : arg.method_14632(false);
		int m = list.size();
		int n = m + list2.size();
		int o = n <= 16 ? 4 : 5;
		int p = (int)Math.ceil((double)((float)n / (float)o));
		this.field_16033 = i;
		this.field_16034 = j;
		int q = 25;
		float g = (float)(this.field_16033 + Math.min(n, o) * 25);
		float h = (float)(k + 50);
		if (g > h) {
			this.field_16033 = (int)((float)this.field_16033 - f * (float)((int)((g - h) / f)));
		}

		float r = (float)(this.field_16034 + p * 25);
		float s = (float)(l + 50);
		if (r > s) {
			this.field_16034 = (int)((float)this.field_16034 - f * (float)MathHelper.ceil((r - s) / f));
		}

		float t = (float)this.field_16034;
		float u = (float)(l - 100);
		if (t < u) {
			this.field_16034 = (int)((float)this.field_16034 - f * (float)MathHelper.ceil((t - u) / f));
		}

		this.field_16032 = true;
		this.field_16031.clear();

		for (int v = 0; v < n; v++) {
			boolean bl2 = v < m;
			this.field_16031
				.add(
					new class_3280.class_3281(
						this.field_16033 + 4 + 25 * (v % o), this.field_16034 + 5 + 25 * (v / o), bl2 ? (RecipeType)list.get(v) : (RecipeType)list2.get(v - m), bl2
					)
				);
		}

		this.field_16037 = null;
	}

	public class_3286 method_14562() {
		return this.field_16036;
	}

	public RecipeType method_14568() {
		return this.field_16037;
	}

	public boolean method_14564(int i, int j, int k) {
		if (k != 0) {
			return false;
		} else {
			for (class_3280.class_3281 lv : this.field_16031) {
				if (lv.isMouseOver(this.field_16035, i, j)) {
					this.field_16037 = lv.field_16040;
					return true;
				}
			}

			return false;
		}
	}

	public void method_14563(int i, int j, float f) {
		if (this.field_16032) {
			this.field_16038 += f;
			DiffuseLighting.enable();
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_16035.getTextureManager().bindTexture(field_16030);
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, 170.0F);
			int k = this.field_16031.size() <= 16 ? 4 : 5;
			int l = Math.min(this.field_16031.size(), k);
			int m = MathHelper.ceil((float)this.field_16031.size() / (float)k);
			int n = 24;
			int o = 4;
			int p = 82;
			int q = 208;
			this.method_14570(l, m, 24, 4, 82, 208);
			GlStateManager.disableBlend();
			DiffuseLighting.disable();

			for (class_3280.class_3281 lv : this.field_16031) {
				lv.method_891(this.field_16035, i, j, f);
			}

			GlStateManager.popMatrix();
		}
	}

	private void method_14570(int i, int j, int k, int l, int m, int n) {
		this.drawTexture(this.field_16033, this.field_16034, m, n, l, l);
		this.drawTexture(this.field_16033 + l * 2 + i * k, this.field_16034, m + k + l, n, l, l);
		this.drawTexture(this.field_16033, this.field_16034 + l * 2 + j * k, m, n + k + l, l, l);
		this.drawTexture(this.field_16033 + l * 2 + i * k, this.field_16034 + l * 2 + j * k, m + k + l, n + k + l, l, l);

		for (int o = 0; o < i; o++) {
			this.drawTexture(this.field_16033 + l + o * k, this.field_16034, m + l, n, k, l);
			this.drawTexture(this.field_16033 + l + (o + 1) * k, this.field_16034, m + l, n, l, l);

			for (int p = 0; p < j; p++) {
				if (o == 0) {
					this.drawTexture(this.field_16033, this.field_16034 + l + p * k, m, n + l, l, k);
					this.drawTexture(this.field_16033, this.field_16034 + l + (p + 1) * k, m, n + l, l, l);
				}

				this.drawTexture(this.field_16033 + l + o * k, this.field_16034 + l + p * k, m + l, n + l, k, k);
				this.drawTexture(this.field_16033 + l + (o + 1) * k, this.field_16034 + l + p * k, m + l, n + l, l, k);
				this.drawTexture(this.field_16033 + l + o * k, this.field_16034 + l + (p + 1) * k, m + l, n + l, k, l);
				this.drawTexture(this.field_16033 + l + (o + 1) * k - 1, this.field_16034 + l + (p + 1) * k - 1, m + l, n + l, l + 1, l + 1);
				if (o == i - 1) {
					this.drawTexture(this.field_16033 + l * 2 + i * k, this.field_16034 + l + p * k, m + k + l, n + l, l, k);
					this.drawTexture(this.field_16033 + l * 2 + i * k, this.field_16034 + l + (p + 1) * k, m + k + l, n + l, l, l);
				}
			}

			this.drawTexture(this.field_16033 + l + o * k, this.field_16034 + l * 2 + j * k, m + l, n + k + l, k, l);
			this.drawTexture(this.field_16033 + l + (o + 1) * k, this.field_16034 + l * 2 + j * k, m + l, n + k + l, l, l);
		}
	}

	public void method_14567(boolean bl) {
		this.field_16032 = bl;
	}

	public boolean method_14569() {
		return this.field_16032;
	}

	class class_3281 extends ButtonWidget {
		private final RecipeType field_16040;
		private final boolean field_16041;

		public class_3281(int i, int j, RecipeType recipeType, boolean bl) {
			super(0, i, j, "");
			this.width = 24;
			this.height = 24;
			this.field_16040 = recipeType;
			this.field_16041 = bl;
		}

		@Override
		public void method_891(MinecraftClient client, int i, int j, float f) {
			DiffuseLighting.enable();
			GlStateManager.enableAlphaTest();
			client.getTextureManager().bindTexture(class_3280.field_16030);
			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			int k = 152;
			if (!this.field_16041) {
				k += 26;
			}

			int l = 78;
			if (this.hovered) {
				l += 26;
			}

			this.drawTexture(this.x, this.y, k, l, this.width, this.height);
			int m = 3;
			int n = 3;
			if (this.field_16040 instanceof ShapedRecipeType) {
				ShapedRecipeType shapedRecipeType = (ShapedRecipeType)this.field_16040;
				m = shapedRecipeType.method_14272();
				n = shapedRecipeType.method_14273();
			}

			Iterator<Ingredient> iterator = this.field_16040.method_14252().iterator();

			for (int o = 0; o < n; o++) {
				int p = 3 + o * 7;

				for (int q = 0; q < m; q++) {
					if (iterator.hasNext()) {
						ItemStack[] itemStacks = ((Ingredient)iterator.next()).method_14244();
						if (itemStacks.length != 0) {
							int r = 3 + q * 7;
							GlStateManager.pushMatrix();
							float g = 0.42F;
							int s = (int)((float)(this.x + r) / 0.42F - 3.0F);
							int t = (int)((float)(this.y + p) / 0.42F - 3.0F);
							GlStateManager.scale(0.42F, 0.42F, 1.0F);
							GlStateManager.enableLighting();
							client.getItemRenderer().method_12461(itemStacks[MathHelper.floor(class_3280.this.field_16038 / 30.0F) % itemStacks.length], s, t);
							GlStateManager.disableLighting();
							GlStateManager.popMatrix();
						}
					}
				}
			}

			GlStateManager.disableAlphaTest();
			DiffuseLighting.disable();
		}
	}
}
