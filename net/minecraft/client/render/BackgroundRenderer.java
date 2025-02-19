package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

public class BackgroundRenderer {
	private static final int field_32685 = 192;
	public static final float field_32684 = 5000.0F;
	private static float red;
	private static float green;
	private static float blue;
	private static int waterFogColor = -1;
	private static int nextWaterFogColor = -1;
	private static long lastWaterFogColorUpdateTime = -1L;

	public static void render(Camera camera, float tickDelta, ClientWorld world, int i, float f) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		Entity entity = camera.getFocusedEntity();
		if (cameraSubmersionType == CameraSubmersionType.WATER) {
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
		} else if (cameraSubmersionType == CameraSubmersionType.LAVA) {
			red = 0.6F;
			green = 0.1F;
			blue = 0.0F;
			lastWaterFogColorUpdateTime = -1L;
		} else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
			red = 0.623F;
			green = 0.734F;
			blue = 0.785F;
			lastWaterFogColorUpdateTime = -1L;
			RenderSystem.clearColor(red, green, blue, 0.0F);
		} else {
			float t = 0.25F + 0.75F * (float)i / 32.0F;
			t = 1.0F - (float)Math.pow((double)t, 0.25);
			Vec3d vec3d = world.method_23777(camera.getPos(), tickDelta);
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
				Vec3f vec3f = new Vec3f(y, 0.0F, 0.0F);
				float z = camera.getHorizontalPlane().dot(vec3f);
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

		double d = (camera.getPos().y - (double)world.getBottomY()) * world.getLevelProperties().getHorizonShadingRatio();
		if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
			int af = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
			if (af < 20) {
				d *= (double)(1.0F - (float)af / 20.0F);
			} else {
				d = 0.0;
			}
		}

		if (d < 1.0 && cameraSubmersionType != CameraSubmersionType.LAVA) {
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

		float ag;
		if (cameraSubmersionType == CameraSubmersionType.WATER) {
			if (entity instanceof ClientPlayerEntity) {
				ag = ((ClientPlayerEntity)entity).getUnderwaterVisibility();
			} else {
				ag = 1.0F;
			}
		} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
			ag = GameRenderer.getNightVisionStrength((LivingEntity)entity, tickDelta);
		} else {
			ag = 0.0F;
		}

		if (red != 0.0F && green != 0.0F && blue != 0.0F) {
			float ak = Math.min(1.0F / red, Math.min(1.0F / green, 1.0F / blue));
			red = red * (1.0F - ag) + red * ak * ag;
			green = green * (1.0F - ag) + green * ak * ag;
			blue = blue * (1.0F - ag) + blue * ak * ag;
		}

		RenderSystem.clearColor(red, green, blue, 0.0F);
	}

	public static void method_23792() {
		RenderSystem.setShaderFogStart(Float.MAX_VALUE);
	}

	public static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		Entity entity = camera.getFocusedEntity();
		if (cameraSubmersionType == CameraSubmersionType.WATER) {
			float f = 192.0F;
			if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
				f *= Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
				Biome biome = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
				if (biome.getCategory() == Biome.Category.SWAMP) {
					f *= 0.85F;
				}
			}

			RenderSystem.setShaderFogStart(-8.0F);
			RenderSystem.setShaderFogEnd(f * 0.5F);
		} else {
			float g;
			float h;
			if (cameraSubmersionType == CameraSubmersionType.LAVA) {
				if (entity.isSpectator()) {
					g = -8.0F;
					h = viewDistance * 0.5F;
				} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
					g = 0.0F;
					h = 3.0F;
				} else {
					g = 0.25F;
					h = 1.0F;
				}
			} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
				int m = ((LivingEntity)entity).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
				float n = MathHelper.lerp(Math.min(1.0F, (float)m / 20.0F), viewDistance, 5.0F);
				if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
					g = 0.0F;
					h = n * 0.8F;
				} else {
					g = n * 0.25F;
					h = n;
				}
			} else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
				if (entity.isSpectator()) {
					g = -8.0F;
					h = viewDistance * 0.5F;
				} else {
					g = 0.0F;
					h = 2.0F;
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

			RenderSystem.setShaderFogStart(g);
			RenderSystem.setShaderFogEnd(h);
		}
	}

	public static void setFogBlack() {
		RenderSystem.setShaderFogColor(red, green, blue);
	}

	public static enum FogType {
		FOG_SKY,
		FOG_TERRAIN;
	}
}
