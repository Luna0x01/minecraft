package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LivingEntityRenderer<T extends LivingEntity> extends EntityRenderer<T> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final NativeImageBackedTexture TEX = new NativeImageBackedTexture(16, 16);
	protected EntityModel model;
	protected FloatBuffer buffer = GlAllocationUtils.allocateFloatBuffer(4);
	protected List<FeatureRenderer<T>> features = Lists.newArrayList();
	protected boolean field_11124;

	public LivingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher);
		this.model = entityModel;
		this.shadowSize = f;
	}

	protected <V extends LivingEntity, U extends FeatureRenderer<V>> boolean addFeature(U featureRenderer) {
		return this.features.add(featureRenderer);
	}

	public EntityModel getModel() {
		return this.model;
	}

	protected float method_5769(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}

	public void translate() {
	}

	public void render(T livingEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		this.model.handSwingProgress = this.method_5787(livingEntity, h);
		this.model.riding = livingEntity.hasMount();
		this.model.child = livingEntity.isBaby();

		try {
			float i = this.method_5769(livingEntity.prevBodyYaw, livingEntity.bodyYaw, h);
			float j = this.method_5769(livingEntity.prevHeadYaw, livingEntity.headYaw, h);
			float k = j - i;
			if (livingEntity.hasMount() && livingEntity.getVehicle() instanceof LivingEntity) {
				LivingEntity livingEntity2 = (LivingEntity)livingEntity.getVehicle();
				i = this.method_5769(livingEntity2.prevBodyYaw, livingEntity2.bodyYaw, h);
				k = j - i;
				float l = MathHelper.wrapDegrees(k);
				if (l < -85.0F) {
					l = -85.0F;
				}

				if (l >= 85.0F) {
					l = 85.0F;
				}

				i = j - l;
				if (l * l > 2500.0F) {
					i += l * 0.2F;
				}
			}

			float m = livingEntity.prevPitch + (livingEntity.pitch - livingEntity.prevPitch) * h;
			this.method_5772(livingEntity, d, e, f);
			float n = this.method_5783(livingEntity, h);
			this.method_5777(livingEntity, n, i, h);
			float o = this.method_12464(livingEntity, h);
			float p = 0.0F;
			float q = 0.0F;
			if (!livingEntity.hasMount()) {
				p = livingEntity.field_6748 + (livingEntity.field_6749 - livingEntity.field_6748) * h;
				q = livingEntity.field_6750 - livingEntity.field_6749 * (1.0F - h);
				if (livingEntity.isBaby()) {
					q *= 3.0F;
				}

				if (p > 1.0F) {
					p = 1.0F;
				}
			}

			GlStateManager.enableAlphaTest();
			this.model.animateModel(livingEntity, q, p, h);
			this.model.setAngles(q, p, n, k, m, o, livingEntity);
			if (this.field_13631) {
				boolean bl = this.method_12463(livingEntity);
				GlStateManager.enableColorMaterial();
				GlStateManager.method_12309(this.method_12454(livingEntity));
				if (!this.field_11124) {
					this.renderModel(livingEntity, q, p, n, k, m, o);
				}

				if (!(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).isSpectator()) {
					this.renderFeatures(livingEntity, q, p, h, n, k, m, o);
				}

				GlStateManager.method_12315();
				GlStateManager.disableColorMaterial();
				if (bl) {
					this.method_10259();
				}
			} else {
				boolean bl2 = this.method_10258(livingEntity, h);
				this.renderModel(livingEntity, q, p, n, k, m, o);
				if (bl2) {
					this.method_10260();
				}

				GlStateManager.depthMask(true);
				if (!(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).isSpectator()) {
					this.renderFeatures(livingEntity, q, p, h, n, k, m, o);
				}
			}

			GlStateManager.disableRescaleNormal();
		} catch (Exception var19) {
			LOGGER.error("Couldn't render entity", var19);
		}

		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.textureUnit);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		super.render(livingEntity, d, e, f, g, h);
	}

	public float method_12464(T livingEntity, float f) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		this.scale(livingEntity, f);
		float g = 0.0625F;
		GlStateManager.translate(0.0F, -1.501F, 0.0F);
		return 0.0625F;
	}

	protected boolean method_12463(T livingEntity) {
		GlStateManager.disableLighting();
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.textureUnit);
		return true;
	}

	protected void method_10259() {
		GlStateManager.enableLighting();
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.textureUnit);
	}

	protected void renderModel(T entity, float f, float g, float h, float i, float j, float k) {
		boolean bl = !entity.isInvisible() || this.field_13631;
		boolean bl2 = !bl && !entity.isInvisibleTo(MinecraftClient.getInstance().player);
		if (bl || bl2) {
			if (!this.bindTexture(entity)) {
				return;
			}

			if (bl2) {
				GlStateManager.method_12286(GlStateManager.class_2869.TRANSPARENT_MODEL);
			}

			this.model.render(entity, f, g, h, i, j, k);
			if (bl2) {
				GlStateManager.method_12299(GlStateManager.class_2869.TRANSPARENT_MODEL);
			}
		}
	}

	protected boolean method_10258(T livingEntity, float f) {
		return this.method_10252(livingEntity, f, true);
	}

	protected boolean method_10252(T livingEntity, float f, boolean bl) {
		float g = livingEntity.getBrightnessAtEyes(f);
		int i = this.method_5776(livingEntity, g, f);
		boolean bl2 = (i >> 24 & 0xFF) > 0;
		boolean bl3 = livingEntity.hurtTime > 0 || livingEntity.deathTime > 0;
		if (!bl2 && !bl3) {
			return false;
		} else if (!bl2 && !bl) {
			return false;
		} else {
			GlStateManager.activeTexture(GLX.textureUnit);
			GlStateManager.enableTexture();
			GlStateManager.method_12274(8960, 8704, GLX.combine);
			GlStateManager.method_12274(8960, GLX.combineRgb, 8448);
			GlStateManager.method_12274(8960, GLX.source0Rgb, GLX.textureUnit);
			GlStateManager.method_12274(8960, GLX.source1Rgb, GLX.primary);
			GlStateManager.method_12274(8960, GLX.operand0Rgb, 768);
			GlStateManager.method_12274(8960, GLX.operand1Rgb, 768);
			GlStateManager.method_12274(8960, GLX.combineAlpha, 7681);
			GlStateManager.method_12274(8960, GLX.source0Alpha, GLX.textureUnit);
			GlStateManager.method_12274(8960, GLX.operand0Alpha, 770);
			GlStateManager.activeTexture(GLX.lightmapTextureUnit);
			GlStateManager.enableTexture();
			GlStateManager.method_12274(8960, 8704, GLX.combine);
			GlStateManager.method_12274(8960, GLX.combineRgb, GLX.interpolate);
			GlStateManager.method_12274(8960, GLX.source0Rgb, GLX.constant);
			GlStateManager.method_12274(8960, GLX.source1Rgb, GLX.previous);
			GlStateManager.method_12274(8960, GLX.source2Rgb, GLX.constant);
			GlStateManager.method_12274(8960, GLX.operand0Rgb, 768);
			GlStateManager.method_12274(8960, GLX.operand1Rgb, 768);
			GlStateManager.method_12274(8960, GLX.operand2Rgb, 770);
			GlStateManager.method_12274(8960, GLX.combineAlpha, 7681);
			GlStateManager.method_12274(8960, GLX.source0Alpha, GLX.previous);
			GlStateManager.method_12274(8960, GLX.operand0Alpha, 770);
			this.buffer.position(0);
			if (bl3) {
				this.buffer.put(1.0F);
				this.buffer.put(0.0F);
				this.buffer.put(0.0F);
				this.buffer.put(0.3F);
			} else {
				float h = (float)(i >> 24 & 0xFF) / 255.0F;
				float j = (float)(i >> 16 & 0xFF) / 255.0F;
				float k = (float)(i >> 8 & 0xFF) / 255.0F;
				float l = (float)(i & 0xFF) / 255.0F;
				this.buffer.put(j);
				this.buffer.put(k);
				this.buffer.put(l);
				this.buffer.put(1.0F - h);
			}

			this.buffer.flip();
			GlStateManager.method_12297(8960, 8705, this.buffer);
			GlStateManager.activeTexture(GLX.texture);
			GlStateManager.enableTexture();
			GlStateManager.bindTexture(TEX.getGlId());
			GlStateManager.method_12274(8960, 8704, GLX.combine);
			GlStateManager.method_12274(8960, GLX.combineRgb, 8448);
			GlStateManager.method_12274(8960, GLX.source0Rgb, GLX.previous);
			GlStateManager.method_12274(8960, GLX.source1Rgb, GLX.lightmapTextureUnit);
			GlStateManager.method_12274(8960, GLX.operand0Rgb, 768);
			GlStateManager.method_12274(8960, GLX.operand1Rgb, 768);
			GlStateManager.method_12274(8960, GLX.combineAlpha, 7681);
			GlStateManager.method_12274(8960, GLX.source0Alpha, GLX.previous);
			GlStateManager.method_12274(8960, GLX.operand0Alpha, 770);
			GlStateManager.activeTexture(GLX.textureUnit);
			return true;
		}
	}

	protected void method_10260() {
		GlStateManager.activeTexture(GLX.textureUnit);
		GlStateManager.enableTexture();
		GlStateManager.method_12274(8960, 8704, GLX.combine);
		GlStateManager.method_12274(8960, GLX.combineRgb, 8448);
		GlStateManager.method_12274(8960, GLX.source0Rgb, GLX.textureUnit);
		GlStateManager.method_12274(8960, GLX.source1Rgb, GLX.primary);
		GlStateManager.method_12274(8960, GLX.operand0Rgb, 768);
		GlStateManager.method_12274(8960, GLX.operand1Rgb, 768);
		GlStateManager.method_12274(8960, GLX.combineAlpha, 8448);
		GlStateManager.method_12274(8960, GLX.source0Alpha, GLX.textureUnit);
		GlStateManager.method_12274(8960, GLX.source1Alpha, GLX.primary);
		GlStateManager.method_12274(8960, GLX.operand0Alpha, 770);
		GlStateManager.method_12274(8960, GLX.operand1Alpha, 770);
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GlStateManager.method_12274(8960, 8704, GLX.combine);
		GlStateManager.method_12274(8960, GLX.combineRgb, 8448);
		GlStateManager.method_12274(8960, GLX.operand0Rgb, 768);
		GlStateManager.method_12274(8960, GLX.operand1Rgb, 768);
		GlStateManager.method_12274(8960, GLX.source0Rgb, 5890);
		GlStateManager.method_12274(8960, GLX.source1Rgb, GLX.previous);
		GlStateManager.method_12274(8960, GLX.combineAlpha, 8448);
		GlStateManager.method_12274(8960, GLX.operand0Alpha, 770);
		GlStateManager.method_12274(8960, GLX.source0Alpha, 5890);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.activeTexture(GLX.texture);
		GlStateManager.disableTexture();
		GlStateManager.bindTexture(0);
		GlStateManager.method_12274(8960, 8704, GLX.combine);
		GlStateManager.method_12274(8960, GLX.combineRgb, 8448);
		GlStateManager.method_12274(8960, GLX.operand0Rgb, 768);
		GlStateManager.method_12274(8960, GLX.operand1Rgb, 768);
		GlStateManager.method_12274(8960, GLX.source0Rgb, 5890);
		GlStateManager.method_12274(8960, GLX.source1Rgb, GLX.previous);
		GlStateManager.method_12274(8960, GLX.combineAlpha, 8448);
		GlStateManager.method_12274(8960, GLX.operand0Alpha, 770);
		GlStateManager.method_12274(8960, GLX.source0Alpha, 5890);
		GlStateManager.activeTexture(GLX.textureUnit);
	}

	protected void method_5772(T livingEntity, double d, double e, double f) {
		GlStateManager.translate((float)d, (float)e, (float)f);
	}

	protected void method_5777(T entity, float f, float g, float h) {
		GlStateManager.rotate(180.0F - g, 0.0F, 1.0F, 0.0F);
		if (entity.deathTime > 0) {
			float i = ((float)entity.deathTime + h - 1.0F) / 20.0F * 1.6F;
			i = MathHelper.sqrt(i);
			if (i > 1.0F) {
				i = 1.0F;
			}

			GlStateManager.rotate(i * this.method_5771(entity), 0.0F, 0.0F, 1.0F);
		} else {
			String string = Formatting.strip(entity.getTranslationKey());
			if (string != null
				&& ("Dinnerbone".equals(string) || "Grumm".equals(string))
				&& (!(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(PlayerModelPart.CAPE))) {
				GlStateManager.translate(0.0F, entity.height + 0.1F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
			}
		}
	}

	protected float method_5787(T livingEntity, float f) {
		return livingEntity.getHandSwingProgress(f);
	}

	protected float method_5783(T entity, float f) {
		return (float)entity.ticksAlive + f;
	}

	protected void renderFeatures(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		for (FeatureRenderer<T> featureRenderer : this.features) {
			boolean bl = this.method_10252(livingEntity, h, featureRenderer.combineTextures());
			featureRenderer.render(livingEntity, f, g, h, i, j, k, l);
			if (bl) {
				this.method_10260();
			}
		}
	}

	protected float method_5771(T entity) {
		return 90.0F;
	}

	protected int method_5776(T livingEntity, float f, float g) {
		return 0;
	}

	protected void scale(T entity, float tickDelta) {
	}

	public void method_10208(T livingEntity, double d, double e, double f) {
		if (this.hasLabel(livingEntity)) {
			double g = livingEntity.squaredDistanceTo(this.dispatcher.field_11098);
			float h = livingEntity.isSneaking() ? 32.0F : 64.0F;
			if (!(g >= (double)(h * h))) {
				String string = livingEntity.getName().asFormattedString();
				GlStateManager.alphaFunc(516, 0.1F);
				this.method_10209(livingEntity, d, e, f, string, g);
			}
		}
	}

	protected boolean hasLabel(T livingEntity) {
		ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
		boolean bl = !livingEntity.isInvisibleTo(clientPlayerEntity);
		if (livingEntity != clientPlayerEntity) {
			AbstractTeam abstractTeam = livingEntity.getScoreboardTeam();
			AbstractTeam abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
			if (abstractTeam != null) {
				AbstractTeam.VisibilityRule visibilityRule = abstractTeam.getNameTagVisibilityRule();
				switch (visibilityRule) {
					case ALWAYS:
						return bl;
					case NEVER:
						return false;
					case HIDE_FOR_OTHER_TEAMS:
						return abstractTeam2 == null ? bl : abstractTeam.isEqual(abstractTeam2) && (abstractTeam.shouldShowFriendlyInvisibles() || bl);
					case HIDE_FOR_OWN_TEAM:
						return abstractTeam2 == null ? bl : !abstractTeam.isEqual(abstractTeam2) && bl;
					default:
						return true;
				}
			}
		}

		return MinecraftClient.isHudEnabled() && livingEntity != this.dispatcher.field_11098 && bl && !livingEntity.hasPassengers();
	}

	static {
		int[] is = TEX.getPixels();

		for (int i = 0; i < 256; i++) {
			is[i] = -1;
		}

		TEX.upload();
	}
}
