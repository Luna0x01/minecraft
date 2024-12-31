package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public abstract class LivingEntityRenderer<T extends LivingEntity> extends EntityRenderer<T> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final NativeImageBackedTexture TEX = new NativeImageBackedTexture(16, 16);
	protected EntityModel model;
	protected FloatBuffer buffer = GlAllocationUtils.allocateFloatBuffer(4);
	protected List<FeatureRenderer<T>> features = Lists.newArrayList();
	protected boolean field_11124 = false;

	public LivingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher);
		this.model = entityModel;
		this.shadowSize = f;
	}

	protected <V extends LivingEntity, U extends FeatureRenderer<V>> boolean addFeature(U featureRenderer) {
		return this.features.add(featureRenderer);
	}

	protected <V extends LivingEntity, U extends FeatureRenderer<V>> boolean removeFeature(U featureRenderer) {
		return this.features.remove(featureRenderer);
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
		this.model.riding = livingEntity.hasVehicle();
		this.model.child = livingEntity.isBaby();

		try {
			float i = this.method_5769(livingEntity.prevBodyYaw, livingEntity.bodyYaw, h);
			float j = this.method_5769(livingEntity.prevHeadYaw, livingEntity.headYaw, h);
			float k = j - i;
			if (livingEntity.hasVehicle() && livingEntity.vehicle instanceof LivingEntity) {
				LivingEntity livingEntity2 = (LivingEntity)livingEntity.vehicle;
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
			GlStateManager.enableRescaleNormal();
			GlStateManager.scale(-1.0F, -1.0F, 1.0F);
			this.scale(livingEntity, h);
			float o = 0.0625F;
			GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
			float p = livingEntity.field_6748 + (livingEntity.field_6749 - livingEntity.field_6748) * h;
			float q = livingEntity.field_6750 - livingEntity.field_6749 * (1.0F - h);
			if (livingEntity.isBaby()) {
				q *= 3.0F;
			}

			if (p > 1.0F) {
				p = 1.0F;
			}

			GlStateManager.enableAlphaTest();
			this.model.animateModel(livingEntity, q, p, h);
			this.model.setAngles(q, p, n, k, m, 0.0625F, livingEntity);
			if (this.field_11124) {
				boolean bl = this.method_10257(livingEntity);
				this.renderModel(livingEntity, q, p, n, k, m, 0.0625F);
				if (bl) {
					this.method_10259();
				}
			} else {
				boolean bl2 = this.method_10258(livingEntity, h);
				this.renderModel(livingEntity, q, p, n, k, m, 0.0625F);
				if (bl2) {
					this.method_10260();
				}

				GlStateManager.depthMask(true);
				if (!(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).isSpectator()) {
					this.renderFeatures(livingEntity, q, p, h, n, k, m, 0.0625F);
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
		if (!this.field_11124) {
			super.render(livingEntity, d, e, f, g, h);
		}
	}

	protected boolean method_10257(T livingEntity) {
		int i = 16777215;
		if (livingEntity instanceof PlayerEntity) {
			Team team = (Team)livingEntity.getScoreboardTeam();
			if (team != null) {
				String string = TextRenderer.getFormattingOnly(team.getPrefix());
				if (string.length() >= 2) {
					i = this.getFontRenderer().getColor(string.charAt(1));
				}
			}
		}

		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		GlStateManager.disableLighting();
		GlStateManager.activeTexture(GLX.textureUnit);
		GlStateManager.color(f, g, h, 1.0F);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.textureUnit);
		return true;
	}

	protected void method_10259() {
		GlStateManager.enableLighting();
		GlStateManager.activeTexture(GLX.textureUnit);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.textureUnit);
	}

	protected void renderModel(T entity, float f, float g, float h, float i, float j, float k) {
		boolean bl = !entity.isInvisible();
		boolean bl2 = !bl && !entity.isInvisibleTo(MinecraftClient.getInstance().player);
		if (bl || bl2) {
			if (!this.bindTexture(entity)) {
				return;
			}

			if (bl2) {
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(770, 771);
				GlStateManager.alphaFunc(516, 0.003921569F);
			}

			this.model.render(entity, f, g, h, i, j, k);
			if (bl2) {
				GlStateManager.disableBlend();
				GlStateManager.alphaFunc(516, 0.1F);
				GlStateManager.popMatrix();
				GlStateManager.depthMask(true);
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
			GL11.glTexEnvi(8960, 8704, GLX.combine);
			GL11.glTexEnvi(8960, GLX.combineRgb, 8448);
			GL11.glTexEnvi(8960, GLX.source0Rgb, GLX.textureUnit);
			GL11.glTexEnvi(8960, GLX.source1Rgb, GLX.primary);
			GL11.glTexEnvi(8960, GLX.operand0Rgb, 768);
			GL11.glTexEnvi(8960, GLX.operand1Rgb, 768);
			GL11.glTexEnvi(8960, GLX.combineAlpha, 7681);
			GL11.glTexEnvi(8960, GLX.source0Alpha, GLX.textureUnit);
			GL11.glTexEnvi(8960, GLX.operand0Alpha, 770);
			GlStateManager.activeTexture(GLX.lightmapTextureUnit);
			GlStateManager.enableTexture();
			GL11.glTexEnvi(8960, 8704, GLX.combine);
			GL11.glTexEnvi(8960, GLX.combineRgb, GLX.interpolate);
			GL11.glTexEnvi(8960, GLX.source0Rgb, GLX.constant);
			GL11.glTexEnvi(8960, GLX.source1Rgb, GLX.previous);
			GL11.glTexEnvi(8960, GLX.source2Rgb, GLX.constant);
			GL11.glTexEnvi(8960, GLX.operand0Rgb, 768);
			GL11.glTexEnvi(8960, GLX.operand1Rgb, 768);
			GL11.glTexEnvi(8960, GLX.operand2Rgb, 770);
			GL11.glTexEnvi(8960, GLX.combineAlpha, 7681);
			GL11.glTexEnvi(8960, GLX.source0Alpha, GLX.previous);
			GL11.glTexEnvi(8960, GLX.operand0Alpha, 770);
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
			GL11.glTexEnv(8960, 8705, this.buffer);
			GlStateManager.activeTexture(GLX.texture);
			GlStateManager.enableTexture();
			GlStateManager.bindTexture(TEX.getGlId());
			GL11.glTexEnvi(8960, 8704, GLX.combine);
			GL11.glTexEnvi(8960, GLX.combineRgb, 8448);
			GL11.glTexEnvi(8960, GLX.source0Rgb, GLX.previous);
			GL11.glTexEnvi(8960, GLX.source1Rgb, GLX.lightmapTextureUnit);
			GL11.glTexEnvi(8960, GLX.operand0Rgb, 768);
			GL11.glTexEnvi(8960, GLX.operand1Rgb, 768);
			GL11.glTexEnvi(8960, GLX.combineAlpha, 7681);
			GL11.glTexEnvi(8960, GLX.source0Alpha, GLX.previous);
			GL11.glTexEnvi(8960, GLX.operand0Alpha, 770);
			GlStateManager.activeTexture(GLX.textureUnit);
			return true;
		}
	}

	protected void method_10260() {
		GlStateManager.activeTexture(GLX.textureUnit);
		GlStateManager.enableTexture();
		GL11.glTexEnvi(8960, 8704, GLX.combine);
		GL11.glTexEnvi(8960, GLX.combineRgb, 8448);
		GL11.glTexEnvi(8960, GLX.source0Rgb, GLX.textureUnit);
		GL11.glTexEnvi(8960, GLX.source1Rgb, GLX.primary);
		GL11.glTexEnvi(8960, GLX.operand0Rgb, 768);
		GL11.glTexEnvi(8960, GLX.operand1Rgb, 768);
		GL11.glTexEnvi(8960, GLX.combineAlpha, 8448);
		GL11.glTexEnvi(8960, GLX.source0Alpha, GLX.textureUnit);
		GL11.glTexEnvi(8960, GLX.source1Alpha, GLX.primary);
		GL11.glTexEnvi(8960, GLX.operand0Alpha, 770);
		GL11.glTexEnvi(8960, GLX.operand1Alpha, 770);
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GL11.glTexEnvi(8960, 8704, GLX.combine);
		GL11.glTexEnvi(8960, GLX.combineRgb, 8448);
		GL11.glTexEnvi(8960, GLX.operand0Rgb, 768);
		GL11.glTexEnvi(8960, GLX.operand1Rgb, 768);
		GL11.glTexEnvi(8960, GLX.source0Rgb, 5890);
		GL11.glTexEnvi(8960, GLX.source1Rgb, GLX.previous);
		GL11.glTexEnvi(8960, GLX.combineAlpha, 8448);
		GL11.glTexEnvi(8960, GLX.operand0Alpha, 770);
		GL11.glTexEnvi(8960, GLX.source0Alpha, 5890);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.activeTexture(GLX.texture);
		GlStateManager.disableTexture();
		GlStateManager.bindTexture(0);
		GL11.glTexEnvi(8960, 8704, GLX.combine);
		GL11.glTexEnvi(8960, GLX.combineRgb, 8448);
		GL11.glTexEnvi(8960, GLX.operand0Rgb, 768);
		GL11.glTexEnvi(8960, GLX.operand1Rgb, 768);
		GL11.glTexEnvi(8960, GLX.source0Rgb, 5890);
		GL11.glTexEnvi(8960, GLX.source1Rgb, GLX.previous);
		GL11.glTexEnvi(8960, GLX.combineAlpha, 8448);
		GL11.glTexEnvi(8960, GLX.operand0Alpha, 770);
		GL11.glTexEnvi(8960, GLX.source0Alpha, 5890);
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
				&& (string.equals("Dinnerbone") || string.equals("Grumm"))
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
				float i = 0.02666667F;
				GlStateManager.alphaFunc(516, 0.1F);
				if (livingEntity.isSneaking()) {
					TextRenderer textRenderer = this.getFontRenderer();
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)d, (float)e + livingEntity.height + 0.5F - (livingEntity.isBaby() ? livingEntity.height / 2.0F : 0.0F), (float)f);
					GL11.glNormal3f(0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(-this.dispatcher.yaw, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(this.dispatcher.pitch, 1.0F, 0.0F, 0.0F);
					GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
					GlStateManager.translate(0.0F, 9.374999F, 0.0F);
					GlStateManager.disableLighting();
					GlStateManager.depthMask(false);
					GlStateManager.enableBlend();
					GlStateManager.disableTexture();
					GlStateManager.blendFuncSeparate(770, 771, 1, 0);
					int j = textRenderer.getStringWidth(string) / 2;
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBuffer();
					bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
					bufferBuilder.vertex((double)(-j - 1), -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
					bufferBuilder.vertex((double)(-j - 1), 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
					bufferBuilder.vertex((double)(j + 1), 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
					bufferBuilder.vertex((double)(j + 1), -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
					tessellator.draw();
					GlStateManager.enableTexture();
					GlStateManager.depthMask(true);
					textRenderer.draw(string, -textRenderer.getStringWidth(string) / 2, 0, 553648127);
					GlStateManager.enableLighting();
					GlStateManager.disableBlend();
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.popMatrix();
				} else {
					this.method_10209(livingEntity, d, e - (livingEntity.isBaby() ? (double)(livingEntity.height / 2.0F) : 0.0), f, string, 0.02666667F, g);
				}
			}
		}
	}

	protected boolean hasLabel(T livingEntity) {
		ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
		if (livingEntity instanceof PlayerEntity && livingEntity != clientPlayerEntity) {
			AbstractTeam abstractTeam = livingEntity.getScoreboardTeam();
			AbstractTeam abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
			if (abstractTeam != null) {
				AbstractTeam.VisibilityRule visibilityRule = abstractTeam.getNameTagVisibilityRule();
				switch (visibilityRule) {
					case ALWAYS:
						return true;
					case NEVER:
						return false;
					case HIDE_FOR_OTHER_TEAMS:
						return abstractTeam2 == null || abstractTeam.isEqual(abstractTeam2);
					case HIDE_FOR_OWN_TEAM:
						return abstractTeam2 == null || !abstractTeam.isEqual(abstractTeam2);
					default:
						return true;
				}
			}
		}

		return MinecraftClient.isHudEnabled()
			&& livingEntity != this.dispatcher.field_11098
			&& !livingEntity.isInvisibleTo(clientPlayerEntity)
			&& livingEntity.rider == null;
	}

	public void method_10253(boolean bl) {
		this.field_11124 = bl;
	}

	static {
		int[] is = TEX.getPixels();

		for (int i = 0; i < 256; i++) {
			is[i] = -1;
		}

		TEX.upload();
	}
}
