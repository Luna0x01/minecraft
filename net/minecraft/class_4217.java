package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import org.lwjgl.opengl.GL;

public class class_4217 {
	private final FloatBuffer field_20647 = GlAllocationUtils.allocateFloatBuffer(16);
	private final FloatBuffer field_20648 = GlAllocationUtils.allocateFloatBuffer(16);
	private float field_20649;
	private float field_20650;
	private float field_20651;
	private float field_20652 = -1.0F;
	private float field_20653 = -1.0F;
	private float field_20654 = -1.0F;
	private int field_20655 = -1;
	private int field_20656 = -1;
	private long field_20657 = -1L;
	private final class_4218 field_20658;
	private final MinecraftClient field_20659;

	public class_4217(class_4218 arg) {
		this.field_20658 = arg;
		this.field_20659 = arg.method_19091();
		this.field_20647.put(0.0F).put(0.0F).put(0.0F).put(1.0F).flip();
	}

	public void method_19053(float f) {
		World world = this.field_20659.world;
		Entity entity = this.field_20659.getCameraEntity();
		BlockState blockState = Camera.method_18135(this.field_20659.world, entity, f);
		FluidState fluidState = Camera.method_18136(this.field_20659.world, entity, f);
		if (fluidState.matches(FluidTags.WATER)) {
			this.method_19056(entity, world, f);
		} else if (fluidState.matches(FluidTags.LAVA)) {
			this.field_20649 = 0.6F;
			this.field_20650 = 0.1F;
			this.field_20651 = 0.0F;
			this.field_20657 = -1L;
		} else {
			this.method_19055(entity, world, f);
			this.field_20657 = -1L;
		}

		double d = (entity.prevTickY + (entity.y - entity.prevTickY) * (double)f) * world.dimension.method_17192();
		if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
			int i = ((LivingEntity)entity).getEffectInstance(StatusEffects.BLINDNESS).getDuration();
			if (i < 20) {
				d *= (double)(1.0F - (float)i / 20.0F);
			} else {
				d = 0.0;
			}
		}

		if (d < 1.0) {
			if (d < 0.0) {
				d = 0.0;
			}

			d *= d;
			this.field_20649 = (float)((double)this.field_20649 * d);
			this.field_20650 = (float)((double)this.field_20650 * d);
			this.field_20651 = (float)((double)this.field_20651 * d);
		}

		if (this.field_20658.method_19078(f) > 0.0F) {
			float g = this.field_20658.method_19078(f);
			this.field_20649 = this.field_20649 * (1.0F - g) + this.field_20649 * 0.7F * g;
			this.field_20650 = this.field_20650 * (1.0F - g) + this.field_20650 * 0.6F * g;
			this.field_20651 = this.field_20651 * (1.0F - g) + this.field_20651 * 0.6F * g;
		}

		if (fluidState.matches(FluidTags.WATER)) {
			float h = 0.0F;
			if (entity instanceof ClientPlayerEntity) {
				ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
				h = clientPlayerEntity.method_19044();
			}

			float j = 1.0F / this.field_20649;
			if (j > 1.0F / this.field_20650) {
				j = 1.0F / this.field_20650;
			}

			if (j > 1.0F / this.field_20651) {
				j = 1.0F / this.field_20651;
			}

			this.field_20649 = this.field_20649 * (1.0F - h) + this.field_20649 * j * h;
			this.field_20650 = this.field_20650 * (1.0F - h) + this.field_20650 * j * h;
			this.field_20651 = this.field_20651 * (1.0F - h) + this.field_20651 * j * h;
		} else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
			float k = this.field_20658.method_19066((LivingEntity)entity, f);
			float l = 1.0F / this.field_20649;
			if (l > 1.0F / this.field_20650) {
				l = 1.0F / this.field_20650;
			}

			if (l > 1.0F / this.field_20651) {
				l = 1.0F / this.field_20651;
			}

			this.field_20649 = this.field_20649 * (1.0F - k) + this.field_20649 * l * k;
			this.field_20650 = this.field_20650 * (1.0F - k) + this.field_20650 * l * k;
			this.field_20651 = this.field_20651 * (1.0F - k) + this.field_20651 * l * k;
		}

		GlStateManager.clearColor(this.field_20649, this.field_20650, this.field_20651, 0.0F);
	}

	private void method_19055(Entity entity, World world, float f) {
		float g = 0.25F + 0.75F * (float)this.field_20659.options.viewDistance / 32.0F;
		g = 1.0F - (float)Math.pow((double)g, 0.25);
		Vec3d vec3d = world.method_3631(this.field_20659.getCameraEntity(), f);
		float h = (float)vec3d.x;
		float i = (float)vec3d.y;
		float j = (float)vec3d.z;
		Vec3d vec3d2 = world.getFogColor(f);
		this.field_20649 = (float)vec3d2.x;
		this.field_20650 = (float)vec3d2.y;
		this.field_20651 = (float)vec3d2.z;
		if (this.field_20659.options.viewDistance >= 4) {
			double d = MathHelper.sin(world.getSkyAngleRadians(f)) > 0.0F ? -1.0 : 1.0;
			Vec3d vec3d3 = new Vec3d(d, 0.0, 0.0);
			float k = (float)entity.getRotationVector(f).dotProduct(vec3d3);
			if (k < 0.0F) {
				k = 0.0F;
			}

			if (k > 0.0F) {
				float[] fs = world.dimension.getBackgroundColor(world.method_16349(f), f);
				if (fs != null) {
					k *= fs[3];
					this.field_20649 = this.field_20649 * (1.0F - k) + fs[0] * k;
					this.field_20650 = this.field_20650 * (1.0F - k) + fs[1] * k;
					this.field_20651 = this.field_20651 * (1.0F - k) + fs[2] * k;
				}
			}
		}

		this.field_20649 = this.field_20649 + (h - this.field_20649) * g;
		this.field_20650 = this.field_20650 + (i - this.field_20650) * g;
		this.field_20651 = this.field_20651 + (j - this.field_20651) * g;
		float l = world.getRainGradient(f);
		if (l > 0.0F) {
			float m = 1.0F - l * 0.5F;
			float n = 1.0F - l * 0.4F;
			this.field_20649 *= m;
			this.field_20650 *= m;
			this.field_20651 *= n;
		}

		float o = world.getThunderGradient(f);
		if (o > 0.0F) {
			float p = 1.0F - o * 0.5F;
			this.field_20649 *= p;
			this.field_20650 *= p;
			this.field_20651 *= p;
		}
	}

	private void method_19056(Entity entity, RenderBlockView renderBlockView, float f) {
		long l = Util.method_20227();
		int i = renderBlockView.method_8577(new BlockPos(Camera.getEntityPos(entity, (double)f))).method_16447();
		if (this.field_20657 < 0L) {
			this.field_20655 = i;
			this.field_20656 = i;
			this.field_20657 = l;
		}

		int j = this.field_20655 >> 16 & 0xFF;
		int k = this.field_20655 >> 8 & 0xFF;
		int m = this.field_20655 & 0xFF;
		int n = this.field_20656 >> 16 & 0xFF;
		int o = this.field_20656 >> 8 & 0xFF;
		int p = this.field_20656 & 0xFF;
		float g = MathHelper.clamp((float)(l - this.field_20657) / 5000.0F, 0.0F, 1.0F);
		float h = (float)n + (float)(j - n) * g;
		float q = (float)o + (float)(k - o) * g;
		float r = (float)p + (float)(m - p) * g;
		this.field_20649 = h / 255.0F;
		this.field_20650 = q / 255.0F;
		this.field_20651 = r / 255.0F;
		if (this.field_20655 != i) {
			this.field_20655 = i;
			this.field_20656 = MathHelper.floor(h) << 16 | MathHelper.floor(q) << 8 | MathHelper.floor(r);
			this.field_20657 = l;
		}
	}

	public void method_19054(int i, float f) {
		Entity entity = this.field_20659.getCameraEntity();
		this.method_19057(false);
		GlStateManager.method_12272(0.0F, -1.0F, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		FluidState fluidState = Camera.method_18136(this.field_20659.world, entity, f);
		if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
			float g = 5.0F;
			int j = ((LivingEntity)entity).getEffectInstance(StatusEffects.BLINDNESS).getDuration();
			if (j < 20) {
				g = 5.0F + (this.field_20658.method_19092() - 5.0F) * (1.0F - (float)j / 20.0F);
			}

			GlStateManager.method_12285(GlStateManager.class_2867.LINEAR);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(g * 0.8F);
			} else {
				GlStateManager.fogStart(g * 0.25F);
				GlStateManager.fogEnd(g);
			}

			if (GL.getCapabilities().GL_NV_fog_distance) {
				GlStateManager.method_12300(34138, 34139);
			}
		} else if (fluidState.matches(FluidTags.WATER)) {
			GlStateManager.method_12285(GlStateManager.class_2867.EXP2);
			if (entity instanceof LivingEntity) {
				if (entity instanceof ClientPlayerEntity) {
					ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
					float h = 0.05F - clientPlayerEntity.method_19044() * clientPlayerEntity.method_19044() * 0.03F;
					Biome biome = clientPlayerEntity.world.method_8577(new BlockPos(clientPlayerEntity));
					if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_M) {
						h += 0.005F;
					}

					GlStateManager.fogDensity(h);
				} else {
					GlStateManager.fogDensity(0.05F);
				}
			} else {
				GlStateManager.fogDensity(0.1F);
			}
		} else if (fluidState.matches(FluidTags.LAVA)) {
			GlStateManager.method_12285(GlStateManager.class_2867.EXP);
			GlStateManager.fogDensity(2.0F);
		} else {
			float k = this.field_20658.method_19092();
			GlStateManager.method_12285(GlStateManager.class_2867.LINEAR);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(k);
			} else {
				GlStateManager.fogStart(k * 0.75F);
				GlStateManager.fogEnd(k);
			}

			if (GL.getCapabilities().GL_NV_fog_distance) {
				GlStateManager.method_12300(34138, 34139);
			}

			if (this.field_20659.world.dimension.isFogThick((int)entity.x, (int)entity.z) || this.field_20659.inGameHud.method_12167().method_12174()) {
				GlStateManager.fogStart(k * 0.05F);
				GlStateManager.fogEnd(Math.min(k, 192.0F) * 0.5F);
			}
		}

		GlStateManager.enableColorMaterial();
		GlStateManager.enableFog();
		GlStateManager.colorMaterial(1028, 4608);
	}

	public void method_19057(boolean bl) {
		if (bl) {
			GlStateManager.method_12298(2918, this.field_20647);
		} else {
			GlStateManager.method_12298(2918, this.method_19052());
		}
	}

	private FloatBuffer method_19052() {
		if (this.field_20652 != this.field_20649 || this.field_20653 != this.field_20650 || this.field_20654 != this.field_20651) {
			this.field_20648.clear();
			this.field_20648.put(this.field_20649).put(this.field_20650).put(this.field_20651).put(1.0F);
			this.field_20648.flip();
			this.field_20652 = this.field_20649;
			this.field_20653 = this.field_20650;
			this.field_20654 = this.field_20651;
		}

		return this.field_20648;
	}
}
