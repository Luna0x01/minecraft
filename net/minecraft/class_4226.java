package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class class_4226 implements AutoCloseable {
	private final NativeImageBackedTexture field_20756;
	private final class_4277 field_20757;
	private final Identifier field_20758;
	private boolean field_20759;
	private float field_20760;
	private float field_20761;
	private final class_4218 field_20762;
	private final MinecraftClient field_20763;

	public class_4226(class_4218 arg) {
		this.field_20762 = arg;
		this.field_20763 = arg.method_19091();
		this.field_20756 = new NativeImageBackedTexture(16, 16, false);
		this.field_20758 = this.field_20763.getTextureManager().registerDynamicTexture("light_map", this.field_20756);
		this.field_20757 = this.field_20756.method_19449();
	}

	public void close() {
		this.field_20756.close();
	}

	public void method_19170() {
		this.field_20761 = (float)((double)this.field_20761 + (Math.random() - Math.random()) * Math.random() * Math.random());
		this.field_20761 = (float)((double)this.field_20761 * 0.9);
		this.field_20760 = this.field_20760 + (this.field_20761 - this.field_20760);
		this.field_20759 = true;
	}

	public void method_19172() {
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.textureUnit);
	}

	public void method_19173() {
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		float f = 0.00390625F;
		GlStateManager.scale(0.00390625F, 0.00390625F, 0.00390625F);
		GlStateManager.translate(8.0F, 8.0F, 8.0F);
		GlStateManager.matrixMode(5888);
		this.field_20763.getTextureManager().bindTexture(this.field_20758);
		GlStateManager.method_12294(3553, 10241, 9729);
		GlStateManager.method_12294(3553, 10240, 9729);
		GlStateManager.method_12294(3553, 10242, 10496);
		GlStateManager.method_12294(3553, 10243, 10496);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.textureUnit);
	}

	public void method_19171(float f) {
		if (this.field_20759) {
			this.field_20763.profiler.push("lightTex");
			World world = this.field_20763.world;
			if (world != null) {
				float g = world.method_3649(1.0F);
				float h = g * 0.95F + 0.05F;
				float i = this.field_20763.player.method_19044();
				float j;
				if (this.field_20763.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
					j = this.field_20762.method_19066(this.field_20763.player, f);
				} else if (i > 0.0F && this.field_20763.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
					j = i;
				} else {
					j = 0.0F;
				}

				for (int m = 0; m < 16; m++) {
					for (int n = 0; n < 16; n++) {
						float o = world.dimension.getLightLevelToBrightness()[m] * h;
						float p = world.dimension.getLightLevelToBrightness()[n] * (this.field_20760 * 0.1F + 1.5F);
						if (world.getLightningTicksLeft() > 0) {
							o = world.dimension.getLightLevelToBrightness()[m];
						}

						float q = o * (g * 0.65F + 0.35F);
						float r = o * (g * 0.65F + 0.35F);
						float u = p * ((p * 0.6F + 0.4F) * 0.6F + 0.4F);
						float v = p * (p * p * 0.6F + 0.4F);
						float w = q + p;
						float x = r + u;
						float y = o + v;
						w = w * 0.96F + 0.03F;
						x = x * 0.96F + 0.03F;
						y = y * 0.96F + 0.03F;
						if (this.field_20762.method_19078(f) > 0.0F) {
							float z = this.field_20762.method_19078(f);
							w = w * (1.0F - z) + w * 0.7F * z;
							x = x * (1.0F - z) + x * 0.6F * z;
							y = y * (1.0F - z) + y * 0.6F * z;
						}

						if (world.dimension.method_11789() == DimensionType.THE_END) {
							w = 0.22F + p * 0.75F;
							x = 0.28F + u * 0.75F;
							y = 0.25F + v * 0.75F;
						}

						if (j > 0.0F) {
							float aa = 1.0F / w;
							if (aa > 1.0F / x) {
								aa = 1.0F / x;
							}

							if (aa > 1.0F / y) {
								aa = 1.0F / y;
							}

							w = w * (1.0F - j) + w * aa * j;
							x = x * (1.0F - j) + x * aa * j;
							y = y * (1.0F - j) + y * aa * j;
						}

						if (w > 1.0F) {
							w = 1.0F;
						}

						if (x > 1.0F) {
							x = 1.0F;
						}

						if (y > 1.0F) {
							y = 1.0F;
						}

						float ab = (float)this.field_20763.options.field_19985;
						float ac = 1.0F - w;
						float ad = 1.0F - x;
						float ae = 1.0F - y;
						ac = 1.0F - ac * ac * ac * ac;
						ad = 1.0F - ad * ad * ad * ad;
						ae = 1.0F - ae * ae * ae * ae;
						w = w * (1.0F - ab) + ac * ab;
						x = x * (1.0F - ab) + ad * ab;
						y = y * (1.0F - ab) + ae * ab;
						w = w * 0.96F + 0.03F;
						x = x * 0.96F + 0.03F;
						y = y * 0.96F + 0.03F;
						if (w > 1.0F) {
							w = 1.0F;
						}

						if (x > 1.0F) {
							x = 1.0F;
						}

						if (y > 1.0F) {
							y = 1.0F;
						}

						if (w < 0.0F) {
							w = 0.0F;
						}

						if (x < 0.0F) {
							x = 0.0F;
						}

						if (y < 0.0F) {
							y = 0.0F;
						}

						int af = 255;
						int ag = (int)(w * 255.0F);
						int ah = (int)(x * 255.0F);
						int ai = (int)(y * 255.0F);
						this.field_20757.method_19460(n, m, 0xFF000000 | ai << 16 | ah << 8 | ag);
					}
				}

				this.field_20756.upload();
				this.field_20759 = false;
				this.field_20763.profiler.pop();
			}
		}
	}
}
