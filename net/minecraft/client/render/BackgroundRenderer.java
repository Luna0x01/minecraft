package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

public class BackgroundRenderer {
	private static float red;
	private static float green;
	private static float blue;
	private static int waterFogColor = -1;
	private static int nextWaterFogColor = -1;
	private static long lastWaterFogColorUpdateTime = -1L;

	public static void render(Camera camera, float tickDelta, ClientWorld world, int i, float f) {
		FluidState fluidState = camera.getSubmergedFluidState();
		if (fluidState.isIn(FluidTags.WATER)) {
			long l = Util.getMeasuringTimeMs();
			int j = world.getBiome(new BlockPos(camera.getPos())).getWaterFogColor();
			if (lastWaterFogColorUpdateTime < 0L) {
				waterFogColor = j;
				nextWaterFogColor = j;
				lastWaterFogColorUpdateTime = l;
			}

			int k = waterFogColor >> 16 & 0xFF;
			int m = waterFogColor >> 8 & 0xFF;
			int n = waterFogColor & 0xFF;
			int o = nextWaterFogColor >> 16 & 0xFF;
			int p = nextWaterFogColor >> 8 & 0xFF;
			int q = nextWaterFogColor & 0xFF;
			float g = MathHelper.clamp((float)(l - lastWaterFogColorUpdateTime) / 5000.0F, 0.0F, 1.0F);
			float h = MathHelper.lerp(g, (float)o, (float)k);
			float r = MathHelper.lerp(g, (float)p, (float)m);
			float s = MathHelper.lerp(g, (float)q, (float)n);
			red = h / 255.0F;
			green = r / 255.0F;
			blue = s / 255.0F;
			if (waterFogColor != j) {
				waterFogColor = j;
				nextWaterFogColor = MathHelper.floor(h) << 16 | MathHelper.floor(r) << 8 | MathHelper.floor(s);
				lastWaterFogColorUpdateTime = l;
			}
		} else if (fluidState.isIn(FluidTags.LAVA)) {
			red = 0.6F;
			green = 0.1F;
			blue = 0.0F;
			lastWaterFogColorUpdateTime = -1L;
		} else {
			float t = 0.25F + 0.75F * (float)i / 32.0F;
			t = 1.0F - (float)Math.pow((double)t, 0.25);
			Vec3d vec3d = world.method_23777(camera.getBlockPos(), tickDelta);
			float u = (float)vec3d.x;
			float v = (float)vec3d.y;
			float w = (float)vec3d.z;
			float x = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(tickDelta) * (float) (Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);
			BiomeAccess biomeAccess = world.getBiomeAccess();
			Vec3d vec3d2 = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
			Vec3d vec3d3 = CubicSampler.sampleColor(
				vec3d2, (ix, jx, k) -> world.getSkyProperties().adjustFogColor(Vec3d.unpackRgb(biomeAccess.getBiomeForNoiseGen(ix, jx, k).getFogColor()), x)
			);
			red = (float)vec3d3.getX();
			green = (float)vec3d3.getY();
			blue = (float)vec3d3.getZ();
			if (i >= 4) {
				float y = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0F ? -1.0F : 1.0F;
				Vector3f vector3f = new Vector3f(y, 0.0F, 0.0F);
				float z = camera.getHorizontalPlane().dot(vector3f);
				if (z < 0.0F) {
					z = 0.0F;
				}

				if (z > 0.0F) {
					float[] fs = world.getSkyProperties().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta);
					if (fs != null) {
						z *= fs[3];
						red = red * (1.0F - z) + fs[0] * z;
						green = green * (1.0F - z) + fs[1] * z;
						blue = blue * (1.0F - z) + fs[2] * z;
					}
				}
			}

			red = red + (u - red) * t;
			green = green + (v - green) * t;
			blue = blue + (w - blue) * t;
			float aa = world.getRainGradient(tickDelta);
			if (aa > 0.0F) {
				float ab = 1.0F - aa * 0.5F;
				float ac = 1.0F - aa * 0.4F;
				red *= ab;
				green *= ab;
				blue *= ac;
			}

			float ad = world.getThunderGradient(tickDelta);
			if (ad > 0.0F) {
				float ae = 1.0F - ad * 0.5F;
				red *= ae;
				green *= ae;
				blue *= ae;
			}

			lastWaterFogColorUpdateTime = -1L;
		}

		double d = camera.getPos().y * world.getLevelProperties().getHorizonShadingRatio();
		if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
			int af = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
			if (af < 20) {
				d *= (double)(1.0F - (float)af / 20.0F);
			} else {
				d = 0.0;
			}
		}

		if (d < 1.0 && !fluidState.isIn(FluidTags.LAVA)) {
			if (d < 0.0) {
				d = 0.0;
			}

			d *= d;
			red = (float)((double)red * d);
			green = (float)((double)green * d);
			blue = (float)((double)blue * d);
		}

		if (f > 0.0F) {
			red = red * (1.0F - f) + red * 0.7F * f;
			green = green * (1.0F - f) + green * 0.6F * f;
			blue = blue * (1.0F - f) + blue * 0.6F * f;
		}

		if (fluidState.isIn(FluidTags.WATER)) {
			float ag = 0.0F;
			if (camera.getFocusedEntity() instanceof ClientPlayerEntity) {
				ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)camera.getFocusedEntity();
				ag = clientPlayerEntity.getUnderwaterVisibility();
			}

			float ah = Math.min(1.0F / red, Math.min(1.0F / green, 1.0F / blue));
			red = red * (1.0F - ag) + red * ah * ag;
			green = green * (1.0F - ag) + green * ah * ag;
			blue = blue * (1.0F - ag) + blue * ah * ag;
		} else if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
			float ai = GameRenderer.getNightVisionStrength((LivingEntity)camera.getFocusedEntity(), tickDelta);
			float aj = Math.min(1.0F / red, Math.min(1.0F / green, 1.0F / blue));
			red = red * (1.0F - ai) + red * aj * ai;
			green = green * (1.0F - ai) + green * aj * ai;
			blue = blue * (1.0F - ai) + blue * aj * ai;
		}

		RenderSystem.clearColor(red, green, blue, 0.0F);
	}

	public static void method_23792() {
		RenderSystem.fogDensity(0.0F);
		RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
	}

	public static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		FluidState fluidState = camera.getSubmergedFluidState();
		Entity entity = camera.getFocusedEntity();
		if (fluidState.isIn(FluidTags.WATER)) {
			float f = 1.0F;
			f = 0.05F;
			if (entity instanceof ClientPlayerEntity) {
				ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
				f -= clientPlayerEntity.getUnderwaterVisibility() * clientPlayerEntity.getUnderwaterVisibility() * 0.03F;
				Biome biome = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
				if (biome.getCategory() == Biome.Category.SWAMP) {
					f += 0.005F;
				}
			}

			RenderSystem.fogDensity(f);
			RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
		} else {
			float g;
			float h;
			if (fluidState.isIn(FluidTags.LAVA)) {
				if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
					g = 0.0F;
					h = 3.0F;
				} else {
					g = 0.25F;
					h = 1.0F;
				}
			} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
				int k = ((LivingEntity)entity).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
				float l = MathHelper.lerp(Math.min(1.0F, (float)k / 20.0F), viewDistance, 5.0F);
				if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
					g = 0.0F;
					h = l * 0.8F;
				} else {
					g = l * 0.25F;
					h = l;
				}
			} else if (thickFog) {
				g = viewDistance * 0.05F;
				h = Math.min(viewDistance, 192.0F) * 0.5F;
			} else if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
				g = 0.0F;
				h = viewDistance;
			} else {
				g = viewDistance * 0.75F;
				h = viewDistance;
			}

			RenderSystem.fogStart(g);
			RenderSystem.fogEnd(h);
			RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
			RenderSystem.setupNvFogDistance();
		}
	}

	public static void setFogBlack() {
		RenderSystem.fog(2918, red, green, blue, 1.0F);
	}

	public static enum FogType {
		FOG_SKY,
		FOG_TERRAIN;
	}
}
