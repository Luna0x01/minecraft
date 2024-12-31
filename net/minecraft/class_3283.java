package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.RecipeBookScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.recipe.RecipeType;

public class class_3283 {
	private List<class_3285> field_16058 = Lists.newArrayListWithCapacity(20);
	private class_3285 field_16059;
	private class_3280 field_16060 = new class_3280();
	private MinecraftClient field_16061;
	private List<class_3287> field_16062 = Lists.newArrayList();
	private List<class_3286> field_16063;
	private class_3257 field_16064;
	private class_3257 field_16065;
	private int field_16066;
	private int field_16067;
	private class_3355 field_16068;
	private RecipeType field_16069;
	private class_3286 field_16070;

	public class_3283() {
		for (int i = 0; i < 20; i++) {
			this.field_16058.add(new class_3285());
		}
	}

	public void method_14606(MinecraftClient minecraftClient, int i, int j) {
		this.field_16061 = minecraftClient;
		this.field_16068 = minecraftClient.player.method_14675();

		for (int k = 0; k < this.field_16058.size(); k++) {
			((class_3285)this.field_16058.get(k)).method_14621(i + 11 + 25 * (k % 5), j + 31 + 25 * (k / 5));
		}

		this.field_16064 = new class_3257(0, i + 93, j + 137, 12, 17, false);
		this.field_16064.method_14477(1, 208, 13, 18, RecipeBookScreen.TEXTURE);
		this.field_16065 = new class_3257(0, i + 38, j + 137, 12, 17, true);
		this.field_16065.method_14477(1, 208, 13, 18, RecipeBookScreen.TEXTURE);
	}

	public void method_14607(RecipeBookScreen recipeBookScreen) {
		this.field_16062.remove(recipeBookScreen);
		this.field_16062.add(recipeBookScreen);
	}

	public void method_14609(List<class_3286> list, boolean bl) {
		this.field_16063 = list;
		this.field_16066 = (int)Math.ceil((double)list.size() / 20.0);
		if (this.field_16066 <= this.field_16067 || bl) {
			this.field_16067 = 0;
		}

		this.method_14612();
	}

	private void method_14612() {
		int i = 20 * this.field_16067;

		for (int j = 0; j < this.field_16058.size(); j++) {
			class_3285 lv = (class_3285)this.field_16058.get(j);
			if (i + j < this.field_16063.size()) {
				class_3286 lv2 = (class_3286)this.field_16063.get(i + j);
				lv.method_14619(lv2, this, this.field_16068);
				lv.visible = true;
			} else {
				lv.visible = false;
			}
		}

		this.method_14613();
	}

	private void method_14613() {
		this.field_16064.visible = this.field_16066 > 1 && this.field_16067 < this.field_16066 - 1;
		this.field_16065.visible = this.field_16066 > 1 && this.field_16067 > 0;
	}

	public void method_14604(int i, int j, int k, int l, float f) {
		if (this.field_16066 > 1) {
			String string = this.field_16067 + 1 + "/" + this.field_16066;
			int m = this.field_16061.textRenderer.getStringWidth(string);
			this.field_16061.textRenderer.draw(string, i - m / 2 + 73, j + 141, -1);
		}

		DiffuseLighting.disable();
		this.field_16059 = null;

		for (class_3285 lv : this.field_16058) {
			lv.method_891(this.field_16061, k, l, f);
			if (lv.visible && lv.isHovered()) {
				this.field_16059 = lv;
			}
		}

		this.field_16065.method_891(this.field_16061, k, l, f);
		this.field_16064.method_891(this.field_16061, k, l, f);
		this.field_16060.method_14563(k, l, f);
	}

	public void method_14603(int i, int j) {
		if (this.field_16061.currentScreen != null && this.field_16059 != null && !this.field_16060.method_14569()) {
			this.field_16061.currentScreen.renderTooltip(this.field_16059.method_14618(this.field_16061.currentScreen), i, j);
		}
	}

	@Nullable
	public RecipeType method_14602() {
		return this.field_16069;
	}

	@Nullable
	public class_3286 method_14610() {
		return this.field_16070;
	}

	public void method_14611() {
		this.field_16060.method_14567(false);
	}

	public boolean method_14605(int i, int j, int k, int l, int m, int n, int o) {
		this.field_16069 = null;
		this.field_16070 = null;
		if (this.field_16060.method_14569()) {
			if (this.field_16060.method_14564(i, j, k)) {
				this.field_16069 = this.field_16060.method_14568();
				this.field_16070 = this.field_16060.method_14562();
			} else {
				this.field_16060.method_14567(false);
			}

			return true;
		} else if (this.field_16064.isMouseOver(this.field_16061, i, j) && k == 0) {
			this.field_16064.playDownSound(this.field_16061.getSoundManager());
			this.field_16067++;
			this.method_14612();
			return true;
		} else if (this.field_16065.isMouseOver(this.field_16061, i, j) && k == 0) {
			this.field_16065.playDownSound(this.field_16061.getSoundManager());
			this.field_16067--;
			this.method_14612();
			return true;
		} else {
			for (class_3285 lv : this.field_16058) {
				if (lv.isMouseOver(this.field_16061, i, j)) {
					lv.playDownSound(this.field_16061.getSoundManager());
					if (k == 0) {
						this.field_16069 = lv.method_14623();
						this.field_16070 = lv.method_14620();
					} else if (!this.field_16060.method_14569() && !lv.method_14622()) {
						this.field_16060.method_14565(this.field_16061, lv.method_14620(), lv.x, lv.y, l + n / 2, m + 13 + o / 2, (float)lv.getWidth(), this.field_16068);
					}

					return true;
				}
			}

			return false;
		}
	}

	public void method_14608(List<RecipeType> list) {
		for (class_3287 lv : this.field_16062) {
			lv.method_14636(list);
		}
	}
}
