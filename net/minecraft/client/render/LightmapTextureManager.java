package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class LightmapTextureManager implements AutoCloseable {
	public static final int field_32767 = 15728880;
	public static final int field_32768 = 15728640;
	public static final int field_32769 = 240;
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
		RenderSystem.setShaderTexture(2, 0);
	}

	public void enable() {
		RenderSystem.setShaderTexture(2, this.textureIdentifier);
		this.client.getTextureManager().bindTexture(this.textureIdentifier);
		RenderSystem.texParameter(3553, 10241, 9729);
		RenderSystem.texParameter(3553, 10240, 9729);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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

				Vec3f vec3f = new Vec3f(f, f, 1.0F);
				vec3f.lerp(new Vec3f(1.0F, 1.0F, 1.0F), 0.35F);
				float m = this.field_21528 + 1.5F;
				Vec3f vec3f2 = new Vec3f();

				for (int n = 0; n < 16; n++) {
					for (int o = 0; o < 16; o++) {
						float p = this.getBrightness(clientWorld, n) * g;
						float q = this.getBrightness(clientWorld, o) * m;
						float s = q * ((q * 0.6F + 0.4F) * 0.6F + 0.4F);
						float t = q * (q * q * 0.6F + 0.4F);
						vec3f2.set(q, s, t);
						if (clientWorld.getSkyProperties().shouldBrightenLighting()) {
							vec3f2.lerp(new Vec3f(0.99F, 1.12F, 1.0F), 0.25F);
						} else {
							Vec3f vec3f3 = vec3f.copy();
							vec3f3.scale(p);
							vec3f2.add(vec3f3);
							vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
							if (this.renderer.getSkyDarkness(delta) > 0.0F) {
								float u = this.renderer.getSkyDarkness(delta);
								Vec3f vec3f4 = vec3f2.copy();
								vec3f4.multiplyComponentwise(0.7F, 0.6F, 0.6F);
								vec3f2.lerp(vec3f4, u);
							}
						}

						vec3f2.clamp(0.0F, 1.0F);
						if (j > 0.0F) {
							float v = Math.max(vec3f2.getX(), Math.max(vec3f2.getY(), vec3f2.getZ()));
							if (v < 1.0F) {
								float w = 1.0F / v;
								Vec3f vec3f5 = vec3f2.copy();
								vec3f5.scale(w);
								vec3f2.lerp(vec3f5, j);
							}
						}

						float x = (float)this.client.options.gamma;
						Vec3f vec3f6 = vec3f2.copy();
						vec3f6.modify(this::method_23795);
						vec3f2.lerp(vec3f6, x);
						vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
						vec3f2.clamp(0.0F, 1.0F);
						vec3f2.scale(255.0F);
						int y = 255;
						int z = (int)vec3f2.getX();
						int aa = (int)vec3f2.getY();
						int ab = (int)vec3f2.getZ();
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

	private float getBrightness(World world, int lightLevel) {
		return world.getDimension().getBrightness(lightLevel);
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
