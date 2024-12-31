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

public class LightmapTextureManager implements AutoCloseable {
	private final NativeImageBackedTexture texture;
	private final NativeImage image;
	private final Identifier textureIdentifier;
	private boolean dirty;
	private float field_21528;
	private final GameRenderer renderer;
	private final MinecraftClient client;

	public LightmapTextureManager(GameRenderer renderer, MinecraftClient client) {
		this.renderer = renderer;
		this.client = client;
		this.texture = new NativeImageBackedTexture(16, 16, false);
		this.textureIdentifier = this.client.getTextureManager().registerDynamicTexture("light_map", this.texture);
		this.image = this.texture.getImage();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				this.image.setPixelColor(j, i, -1);
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
		this.dirty = true;
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

	public void update(float delta) {
		if (this.dirty) {
			this.dirty = false;
			this.client.getProfiler().push("lightTex");
			ClientWorld clientWorld = this.client.world;
			if (clientWorld != null) {
				float f = clientWorld.method_23783(1.0F);
				float g;
				if (clientWorld.getLightningTicksLeft() > 0) {
					g = 1.0F;
				} else {
					g = f * 0.95F + 0.05F;
				}

				float i = this.client.player.getUnderwaterVisibility();
				float j;
				if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
					j = GameRenderer.getNightVisionStrength(this.client.player, delta);
				} else if (i > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
					j = i;
				} else {
					j = 0.0F;
				}

				Vector3f vector3f = new Vector3f(f, f, 1.0F);
				vector3f.lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
				float m = this.field_21528 + 1.5F;
				Vector3f vector3f2 = new Vector3f();

				for (int n = 0; n < 16; n++) {
					for (int o = 0; o < 16; o++) {
						float p = this.getBrightness(clientWorld, n) * g;
						float q = this.getBrightness(clientWorld, o) * m;
						float s = q * ((q * 0.6F + 0.4F) * 0.6F + 0.4F);
						float t = q * (q * q * 0.6F + 0.4F);
						vector3f2.set(q, s, t);
						if (clientWorld.getSkyProperties().shouldBrightenLighting()) {
							vector3f2.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
						} else {
							Vector3f vector3f3 = vector3f.copy();
							vector3f3.scale(p);
							vector3f2.add(vector3f3);
							vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
							if (this.renderer.getSkyDarkness(delta) > 0.0F) {
								float u = this.renderer.getSkyDarkness(delta);
								Vector3f vector3f4 = vector3f2.copy();
								vector3f4.multiplyComponentwise(0.7F, 0.6F, 0.6F);
								vector3f2.lerp(vector3f4, u);
							}
						}

						vector3f2.clamp(0.0F, 1.0F);
						if (j > 0.0F) {
							float v = Math.max(vector3f2.getX(), Math.max(vector3f2.getY(), vector3f2.getZ()));
							if (v < 1.0F) {
								float w = 1.0F / v;
								Vector3f vector3f5 = vector3f2.copy();
								vector3f5.scale(w);
								vector3f2.lerp(vector3f5, j);
							}
						}

						float x = (float)this.client.options.gamma;
						Vector3f vector3f6 = vector3f2.copy();
						vector3f6.modify(this::method_23795);
						vector3f2.lerp(vector3f6, x);
						vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
						vector3f2.clamp(0.0F, 1.0F);
						vector3f2.scale(255.0F);
						int y = 255;
						int z = (int)vector3f2.getX();
						int aa = (int)vector3f2.getY();
						int ab = (int)vector3f2.getZ();
						this.image.setPixelColor(o, n, 0xFF000000 | ab << 16 | aa << 8 | z);
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
		return world.getDimension().method_28516(i);
	}

	public static int pack(int block, int sky) {
		return block << 4 | sky << 20;
	}

	public static int getBlockLightCoordinates(int light) {
		return light >> 4 & 65535;
	}

	public static int getSkyLightCoordinates(int light) {
		return light >> 20 & 65535;
	}
}
