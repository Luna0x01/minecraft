package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class class_3266 implements class_3262 {
	private final class_3266.class_3267 field_15928;
	private final String field_15929;
	private final String field_15930;
	private class_3262.class_3263 field_15931 = class_3262.class_3263.SHOW;
	private long field_15932;
	private float field_15933;
	private float field_15934;
	private final boolean field_15935;

	public class_3266(class_3266.class_3267 arg, Text text, @Nullable Text text2, boolean bl) {
		this.field_15928 = arg;
		this.field_15929 = text.asFormattedString();
		this.field_15930 = text2 == null ? null : text2.asFormattedString();
		this.field_15935 = bl;
	}

	@Override
	public class_3262.class_3263 method_14486(class_3264 arg, long l) {
		arg.method_14494().getTextureManager().bindTexture(field_15914);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		arg.drawTexture(0, 0, 0, 96, 160, 32);
		this.field_15928.method_14500(arg, 6, 6);
		if (this.field_15930 == null) {
			arg.method_14494().textRenderer.method_18355(this.field_15929, 30.0F, 12.0F, -11534256);
		} else {
			arg.method_14494().textRenderer.method_18355(this.field_15929, 30.0F, 7.0F, -11534256);
			arg.method_14494().textRenderer.method_18355(this.field_15930, 30.0F, 18.0F, -16777216);
		}

		if (this.field_15935) {
			DrawableHelper.fill(3, 28, 157, 29, -1);
			float f = (float)MathHelper.clampedLerp((double)this.field_15933, (double)this.field_15934, (double)((float)(l - this.field_15932) / 100.0F));
			int i;
			if (this.field_15934 >= this.field_15933) {
				i = -16755456;
			} else {
				i = -11206656;
			}

			DrawableHelper.fill(3, 28, (int)(3.0F + 154.0F * f), 29, i);
			this.field_15933 = f;
			this.field_15932 = l;
		}

		return this.field_15931;
	}

	public void method_14498() {
		this.field_15931 = class_3262.class_3263.HIDE;
	}

	public void method_14499(float f) {
		this.field_15934 = f;
	}

	public static enum class_3267 {
		MOVEMENT_KEYS(0, 0),
		MOUSE(1, 0),
		TREE(2, 0),
		RECIPE_BOOK(0, 1),
		WOODEN_PLANKS(1, 1);

		private final int field_15941;
		private final int field_15942;

		private class_3267(int j, int k) {
			this.field_15941 = j;
			this.field_15942 = k;
		}

		public void method_14500(DrawableHelper drawableHelper, int i, int j) {
			GlStateManager.enableBlend();
			drawableHelper.drawTexture(i, j, 176 + this.field_15941 * 20, this.field_15942 * 20, 20, 20);
			GlStateManager.enableBlend();
		}
	}
}
