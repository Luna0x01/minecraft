package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class LightmapTextureManager implements AutoCloseable {
	private final NativeImageBackedTexture texture;
	private final NativeImage image;
	private final Identifier textureIdentifier;
	private boolean isDirty;
	private float prevFlicker;
	private float flicker;
	private final GameRenderer worldRenderer;
	private final MinecraftClient client;

	public LightmapTextureManager(GameRenderer gameRenderer) {
		this.worldRenderer = gameRenderer;
		this.client = gameRenderer.getClient();
		this.texture = new NativeImageBackedTexture(16, 16, false);
		this.textureIdentifier = this.client.getTextureManager().registerDynamicTexture("light_map", this.texture);
		this.image = this.texture.getImage();
	}

	public void close() {
		this.texture.close();
	}

	public void tick() {
		this.flicker = (float)((double)this.flicker + (Math.random() - Math.random()) * Math.random() * Math.random());
		this.flicker = (float)((double)this.flicker * 0.9);
		this.prevFlicker = this.prevFlicker + (this.flicker - this.prevFlicker);
		this.isDirty = true;
	}

	public void disable() {
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	public void enable() {
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		float f = 0.00390625F;
		GlStateManager.scalef(0.00390625F, 0.00390625F, 0.00390625F);
		GlStateManager.translatef(8.0F, 8.0F, 8.0F);
		GlStateManager.matrixMode(5888);
		this.client.getTextureManager().bindTexture(this.textureIdentifier);
		GlStateManager.texParameter(3553, 10241, 9729);
		GlStateManager.texParameter(3553, 10240, 9729);
		GlStateManager.texParameter(3553, 10242, 10496);
		GlStateManager.texParameter(3553, 10243, 10496);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	public void update(float f) {
		if (this.isDirty) {
			this.client.getProfiler().push("lightTex");
			World world = this.client.world;
			if (world != null) {
				float g = world.getAmbientLight(1.0F);
				float h = g * 0.95F + 0.05F;
				float i = this.client.player.method_3140();
				float j;
				if (this.client.player.hasStatusEffect(StatusEffects.field_5925)) {
					j = this.worldRenderer.getNightVisionStrength(this.client.player, f);
				} else if (i > 0.0F && this.client.player.hasStatusEffect(StatusEffects.field_5927)) {
					j = i;
				} else {
					j = 0.0F;
				}

				for (int m = 0; m < 16; m++) {
					for (int n = 0; n < 16; n++) {
						float o = world.dimension.getLightLevelToBrightness()[m] * h;
						float p = world.dimension.getLightLevelToBrightness()[n] * (this.prevFlicker * 0.1F + 1.5F);
						if (world.getTicksSinceLightning() > 0) {
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
						if (this.worldRenderer.getSkyDarkness(f) > 0.0F) {
							float z = this.worldRenderer.getSkyDarkness(f);
							w = w * (1.0F - z) + w * 0.7F * z;
							x = x * (1.0F - z) + x * 0.6F * z;
							y = y * (1.0F - z) + y * 0.6F * z;
						}

						if (world.dimension.getType() == DimensionType.field_13078) {
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

						float ab = (float)this.client.options.gamma;
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
						this.image.setPixelRGBA(n, m, 0xFF000000 | ai << 16 | ah << 8 | ag);
					}
				}

				this.texture.upload();
				this.isDirty = false;
				this.client.getProfiler().pop();
			}
		}
	}
}
