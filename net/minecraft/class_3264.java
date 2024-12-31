package net.minecraft;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class class_3264 extends DrawableHelper {
	private final MinecraftClient field_15920;
	private final class_3264.class_3265<?>[] field_15921 = new class_3264.class_3265[5];
	private final Deque<class_3262> field_15922 = Queues.newArrayDeque();

	public class_3264(MinecraftClient minecraftClient) {
		this.field_15920 = minecraftClient;
	}

	public void method_18450() {
		if (!this.field_15920.options.field_19987) {
			DiffuseLighting.disable();

			for (int i = 0; i < this.field_15921.length; i++) {
				class_3264.class_3265<?> lv = this.field_15921[i];
				if (lv != null && lv.method_14496(this.field_15920.field_19944.method_18321(), i)) {
					this.field_15921[i] = null;
				}

				if (this.field_15921[i] == null && !this.field_15922.isEmpty()) {
					this.field_15921[i] = new class_3264.class_3265((class_3262)this.field_15922.removeFirst());
				}
			}
		}
	}

	@Nullable
	public <T extends class_3262> T method_14493(Class<? extends T> class_, Object object) {
		for (class_3264.class_3265<?> lv : this.field_15921) {
			if (lv != null && class_.isAssignableFrom(lv.method_14495().getClass()) && lv.method_14495().method_14487().equals(object)) {
				return (T)lv.method_14495();
			}
		}

		for (class_3262 lv2 : this.field_15922) {
			if (class_.isAssignableFrom(lv2.getClass()) && lv2.method_14487().equals(object)) {
				return (T)lv2;
			}
		}

		return null;
	}

	public void method_14489() {
		Arrays.fill(this.field_15921, null);
		this.field_15922.clear();
	}

	public void method_14491(class_3262 arg) {
		this.field_15922.add(arg);
	}

	public MinecraftClient method_14494() {
		return this.field_15920;
	}

	class class_3265<T extends class_3262> {
		private final T field_15924;
		private long field_15925 = -1L;
		private long field_15926 = -1L;
		private class_3262.class_3263 field_15927 = class_3262.class_3263.SHOW;

		private class_3265(T arg2) {
			this.field_15924 = arg2;
		}

		public T method_14495() {
			return this.field_15924;
		}

		private float method_14497(long l) {
			float f = MathHelper.clamp((float)(l - this.field_15925) / 600.0F, 0.0F, 1.0F);
			f *= f;
			return this.field_15927 == class_3262.class_3263.HIDE ? 1.0F - f : f;
		}

		public boolean method_14496(int i, int j) {
			long l = Util.method_20227();
			if (this.field_15925 == -1L) {
				this.field_15925 = l;
				this.field_15927.method_14488(class_3264.this.field_15920.getSoundManager());
			}

			if (this.field_15927 == class_3262.class_3263.SHOW && l - this.field_15925 <= 600L) {
				this.field_15926 = l;
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate((float)i - 160.0F * this.method_14497(l), (float)(j * 32), (float)(500 + j));
			class_3262.class_3263 lv = this.field_15924.method_14486(class_3264.this, l - this.field_15926);
			GlStateManager.popMatrix();
			if (lv != this.field_15927) {
				this.field_15925 = l - (long)((int)((1.0F - this.method_14497(l)) * 600.0F));
				this.field_15927 = lv;
				this.field_15927.method_14488(class_3264.this.field_15920.getSoundManager());
			}

			return this.field_15927 == class_3262.class_3263.HIDE && l - this.field_15925 > 600L;
		}
	}
}
