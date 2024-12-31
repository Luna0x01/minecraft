package net.minecraft.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.SoundContainerImpl;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_2841 extends DrawableHelper implements class_2907 {
	private final MinecraftClient field_13313;
	private final List<class_2841.class_2842> field_13314 = Lists.newArrayList();
	private boolean field_13315;

	public class_2841(MinecraftClient minecraftClient) {
		this.field_13313 = minecraftClient;
	}

	public void method_12176(Window window) {
		if (!this.field_13315 && this.field_13313.options.field_13292) {
			this.field_13313.getSoundManager().method_12543(this);
			this.field_13315 = true;
		} else if (this.field_13315 && !this.field_13313.options.field_13292) {
			this.field_13313.getSoundManager().method_12546(this);
			this.field_13315 = false;
		}

		if (this.field_13315 && !this.field_13314.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			Vec3d vec3d = new Vec3d(this.field_13313.player.x, this.field_13313.player.y + (double)this.field_13313.player.getEyeHeight(), this.field_13313.player.z);
			Vec3d vec3d2 = new Vec3d(0.0, 0.0, -1.0)
				.rotateX(-this.field_13313.player.pitch * (float) (Math.PI / 180.0))
				.rotateY(-this.field_13313.player.yaw * (float) (Math.PI / 180.0));
			Vec3d vec3d3 = new Vec3d(0.0, 1.0, 0.0)
				.rotateX(-this.field_13313.player.pitch * (float) (Math.PI / 180.0))
				.rotateY(-this.field_13313.player.yaw * (float) (Math.PI / 180.0));
			Vec3d vec3d4 = vec3d2.crossProduct(vec3d3);
			int i = 0;
			int j = 0;
			Iterator<class_2841.class_2842> iterator = this.field_13314.iterator();

			while (iterator.hasNext()) {
				class_2841.class_2842 lv = (class_2841.class_2842)iterator.next();
				if (lv.method_12179() + 3000L <= MinecraftClient.getTime()) {
					iterator.remove();
				} else {
					j = Math.max(j, this.field_13313.textRenderer.getStringWidth(lv.method_12177()));
				}
			}

			j += this.field_13313.textRenderer.getStringWidth("<")
				+ this.field_13313.textRenderer.getStringWidth(" ")
				+ this.field_13313.textRenderer.getStringWidth(">")
				+ this.field_13313.textRenderer.getStringWidth(" ");

			for (class_2841.class_2842 lv2 : this.field_13314) {
				int k = 255;
				String string = lv2.method_12177();
				Vec3d vec3d5 = lv2.method_12180().subtract(vec3d).normalize();
				double d = -vec3d4.dotProduct(vec3d5);
				double e = -vec3d2.dotProduct(vec3d5);
				boolean bl = e > 0.5;
				int l = j / 2;
				int m = this.field_13313.textRenderer.fontHeight;
				int n = m / 2;
				float f = 1.0F;
				int o = this.field_13313.textRenderer.getStringWidth(string);
				int p = MathHelper.floor(MathHelper.clampedLerp(255.0, 75.0, (double)((float)(MinecraftClient.getTime() - lv2.method_12179()) / 3000.0F)));
				int q = p << 16 | p << 8 | p;
				GlStateManager.pushMatrix();
				GlStateManager.translate((float)window.getWidth() - (float)l * 1.0F - 2.0F, (float)(window.getHeight() - 30) - (float)(i * (m + 1)) * 1.0F, 0.0F);
				GlStateManager.scale(1.0F, 1.0F, 1.0F);
				fill(-l - 1, -n - 1, l + 1, n + 1, -872415232);
				GlStateManager.enableBlend();
				if (!bl) {
					if (d > 0.0) {
						this.field_13313.textRenderer.draw(">", l - this.field_13313.textRenderer.getStringWidth(">"), -n, q + -16777216);
					} else if (d < 0.0) {
						this.field_13313.textRenderer.draw("<", -l, -n, q + -16777216);
					}
				}

				this.field_13313.textRenderer.draw(string, -o / 2, -n, q + -16777216);
				GlStateManager.popMatrix();
				i++;
			}

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void method_12541(SoundInstance soundInstance, SoundContainerImpl soundContainerImpl) {
		if (soundContainerImpl.method_12551() != null) {
			String string = soundContainerImpl.method_12551().asFormattedString();
			if (!this.field_13314.isEmpty()) {
				for (class_2841.class_2842 lv : this.field_13314) {
					if (lv.method_12177().equals(string)) {
						lv.method_12178(new Vec3d((double)soundInstance.getX(), (double)soundInstance.getY(), (double)soundInstance.getZ()));
						return;
					}
				}
			}

			this.field_13314.add(new class_2841.class_2842(string, new Vec3d((double)soundInstance.getX(), (double)soundInstance.getY(), (double)soundInstance.getZ())));
		}
	}

	public class class_2842 {
		private final String field_13317;
		private long field_13318;
		private Vec3d field_13319;

		public class_2842(String string, Vec3d vec3d) {
			this.field_13317 = string;
			this.field_13319 = vec3d;
			this.field_13318 = MinecraftClient.getTime();
		}

		public String method_12177() {
			return this.field_13317;
		}

		public long method_12179() {
			return this.field_13318;
		}

		public Vec3d method_12180() {
			return this.field_13319;
		}

		public void method_12178(Vec3d vec3d) {
			this.field_13319 = vec3d;
			this.field_13318 = MinecraftClient.getTime();
		}
	}
}
