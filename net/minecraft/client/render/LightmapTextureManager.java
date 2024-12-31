package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class LightmapTextureManager implements AutoCloseable {
	private final NativeImageBackedTexture texture;
	private final NativeImage image;
	private final Identifier textureIdentifier;
	private boolean isDirty;
	private float field_21528;
	private final GameRenderer worldRenderer;
	private final MinecraftClient client;

	public LightmapTextureManager(GameRenderer gameRenderer, MinecraftClient minecraftClient) {
		this.worldRenderer = gameRenderer;
		this.client = minecraftClient;
		this.texture = new NativeImageBackedTexture(16, 16, false);
		this.textureIdentifier = this.client.getTextureManager().registerDynamicTexture("light_map", this.texture);
		this.image = this.texture.getImage();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				this.image.setPixelRgba(j, i, -1);
			}
		}

		this.texture.upload();
	}

	public void close() {
		this.texture.close();
	}

	public void tick() {
		this.field_21528 = (float)((double)this.field_21528 + (Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
		this.field_21528 = (float)((double)this.field_21528 * 0.9);
		this.isDirty = true;
	}

	public void disable() {
		RenderSystem.activeTexture(33986);
		RenderSystem.disableTexture();
		RenderSystem.activeTexture(33984);
	}

	public void enable() {
		RenderSystem.activeTexture(33986);
		RenderSystem.matrixMode(5890);
		RenderSystem.loadIdentity();
		float f = 0.00390625F;
		RenderSystem.scalef(0.00390625F, 0.00390625F, 0.00390625F);
		RenderSystem.translatef(8.0F, 8.0F, 8.0F);
		RenderSystem.matrixMode(5888);
		this.client.getTextureManager().bindTexture(this.textureIdentifier);
		RenderSystem.texParameter(3553, 10241, 9729);
		RenderSystem.texParameter(3553, 10240, 9729);
		RenderSystem.texParameter(3553, 10242, 10496);
		RenderSystem.texParameter(3553, 10243, 10496);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableTexture();
		RenderSystem.activeTexture(33984);
	}

	public void update(float f) {
		if (this.isDirty) {
			this.isDirty = false;
			this.client.getProfiler().push("lightTex");
			ClientWorld clientWorld = this.client.world;
			if (clientWorld != null) {
				float g = clientWorld.method_23783(1.0F);
				float h;
				if (clientWorld.getLightningTicksLeft() > 0) {
					h = 1.0F;
				} else {
					h = g * 0.95F + 0.05F;
				}

				float j = this.client.player.method_3140();
				float k;
				if (this.client.player.hasStatusEffect(StatusEffects.field_5925)) {
					k = GameRenderer.getNightVisionStrength(this.client.player, f);
				} else if (j > 0.0F && this.client.player.hasStatusEffect(StatusEffects.field_5927)) {
					k = j;
				} else {
					k = 0.0F;
				}

				Vector3f vector3f = new Vector3f(g, g, 1.0F);
				vector3f.lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
				float n = this.field_21528 + 1.5F;
				Vector3f vector3f2 = new Vector3f();

				for (int o = 0; o < 16; o++) {
					for (int p = 0; p < 16; p++) {
						float q = this.getBrightness(clientWorld, o) * h;
						float r = this.getBrightness(clientWorld, p) * n;
						float t = r * ((r * 0.6F + 0.4F) * 0.6F + 0.4F);
						float u = r * (r * r * 0.6F + 0.4F);
						vector3f2.set(r, t, u);
						if (clientWorld.dimension.getType() == DimensionType.field_13078) {
							vector3f2.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
						} else {
							Vector3f vector3f3 = vector3f.copy();
							vector3f3.scale(q);
							vector3f2.add(vector3f3);
							vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
							if (this.worldRenderer.getSkyDarkness(f) > 0.0F) {
								float v = this.worldRenderer.getSkyDarkness(f);
								Vector3f vector3f4 = vector3f2.copy();
								vector3f4.multiplyComponentwise(0.7F, 0.6F, 0.6F);
								vector3f2.lerp(vector3f4, v);
							}
						}

						vector3f2.clamp(0.0F, 1.0F);
						if (k > 0.0F) {
							float w = Math.max(vector3f2.getX(), Math.max(vector3f2.getY(), vector3f2.getZ()));
							if (w < 1.0F) {
								float x = 1.0F / w;
								Vector3f vector3f5 = vector3f2.copy();
								vector3f5.scale(x);
								vector3f2.lerp(vector3f5, k);
							}
						}

						float y = (float)this.client.options.gamma;
						Vector3f vector3f6 = vector3f2.copy();
						vector3f6.modify(this::method_23795);
						vector3f2.lerp(vector3f6, y);
						vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
						vector3f2.clamp(0.0F, 1.0F);
						vector3f2.scale(255.0F);
						int z = 255;
						int aa = (int)vector3f2.getX();
						int ab = (int)vector3f2.getY();
						int ac = (int)vector3f2.getZ();
						this.image.setPixelRgba(p, o, 0xFF000000 | ac << 16 | ab << 8 | aa);
					}
				}

				this.texture.upload();
				this.client.getProfiler().pop();
			}
		}
	}

	private float method_23795(float f) {
		float g = 1.0F - f;
		return 1.0F - g * g * g * g;
	}

	private float getBrightness(World world, int i) {
		return world.dimension.getBrightness(i);
	}

	public static int pack(int i, int j) {
		return i << 4 | j << 20;
	}

	public static int getBlockLightCoordinates(int i) {
		return i >> 4 & 65535;
	}

	public static int getSkyLightCoordinates(int i) {
		return i >> 20 & 65535;
	}
}
