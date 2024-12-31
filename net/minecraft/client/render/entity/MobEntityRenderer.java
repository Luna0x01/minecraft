package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.CameraView;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.MobEntity;

public abstract class MobEntityRenderer<T extends MobEntity> extends LivingEntityRenderer<T> {
	public MobEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
	}

	protected boolean hasLabel(T mobEntity) {
		return super.hasLabel(mobEntity) && (mobEntity.shouldRenderName() || mobEntity.hasCustomName() && mobEntity == this.dispatcher.field_7998);
	}

	public boolean shouldRender(T mobEntity, CameraView cameraView, double d, double e, double f) {
		if (super.shouldRender(mobEntity, cameraView, d, e, f)) {
			return true;
		} else if (mobEntity.isLeashed() && mobEntity.getLeashOwner() != null) {
			Entity entity = mobEntity.getLeashOwner();
			return cameraView.isBoxInFrustum(entity.getVisibilityBoundingBox());
		} else {
			return false;
		}
	}

	public void render(T mobEntity, double d, double e, double f, float g, float h) {
		super.render(mobEntity, d, e, f, g, h);
		if (!this.field_13631) {
			this.method_5792(mobEntity, d, e, f, g, h);
		}
	}

	public void method_14692(T mobEntity) {
		int i = mobEntity.getLightmapCoordinates();
		int j = i % 65536;
		int k = i / 65536;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)j, (float)k);
	}

	private double method_5790(double d, double e, double f) {
		return d + (e - d) * f;
	}

	protected void method_5792(T mobEntity, double d, double e, double f, float g, float h) {
		Entity entity = mobEntity.getLeashOwner();
		if (entity != null) {
			e -= (1.6 - (double)mobEntity.height) * 0.5;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			double i = this.method_5790((double)entity.prevYaw, (double)entity.yaw, (double)(h * 0.5F)) * (float) (Math.PI / 180.0);
			double j = this.method_5790((double)entity.prevPitch, (double)entity.pitch, (double)(h * 0.5F)) * (float) (Math.PI / 180.0);
			double k = Math.cos(i);
			double l = Math.sin(i);
			double m = Math.sin(j);
			if (entity instanceof AbstractDecorationEntity) {
				k = 0.0;
				l = 0.0;
				m = -1.0;
			}

			double n = Math.cos(j);
			double o = this.method_5790(entity.prevX, entity.x, (double)h) - k * 0.7 - l * 0.5 * n;
			double p = this.method_5790(entity.prevY + (double)entity.getEyeHeight() * 0.7, entity.y + (double)entity.getEyeHeight() * 0.7, (double)h) - m * 0.5 - 0.25;
			double q = this.method_5790(entity.prevZ, entity.z, (double)h) - l * 0.7 + k * 0.5 * n;
			double r = this.method_5790((double)mobEntity.prevBodyYaw, (double)mobEntity.bodyYaw, (double)h) * (float) (Math.PI / 180.0) + (Math.PI / 2);
			k = Math.cos(r) * (double)mobEntity.width * 0.4;
			l = Math.sin(r) * (double)mobEntity.width * 0.4;
			double s = this.method_5790(mobEntity.prevX, mobEntity.x, (double)h) + k;
			double t = this.method_5790(mobEntity.prevY, mobEntity.y, (double)h);
			double u = this.method_5790(mobEntity.prevZ, mobEntity.z, (double)h) + l;
			d += k;
			f += l;
			double v = (double)((float)(o - s));
			double w = (double)((float)(p - t));
			double x = (double)((float)(q - u));
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			int y = 24;
			double z = 0.025;
			bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

			for (int aa = 0; aa <= 24; aa++) {
				float ab = 0.5F;
				float ac = 0.4F;
				float ad = 0.3F;
				if (aa % 2 == 0) {
					ab *= 0.7F;
					ac *= 0.7F;
					ad *= 0.7F;
				}

				float ae = (float)aa / 24.0F;
				bufferBuilder.vertex(d + v * (double)ae + 0.0, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0F - (float)aa) / 18.0F + 0.125F), f + x * (double)ae)
					.color(ab, ac, ad, 1.0F)
					.next();
				bufferBuilder.vertex(
						d + v * (double)ae + 0.025, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0F - (float)aa) / 18.0F + 0.125F) + 0.025, f + x * (double)ae
					)
					.color(ab, ac, ad, 1.0F)
					.next();
			}

			tessellator.draw();
			bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

			for (int af = 0; af <= 24; af++) {
				float ag = 0.5F;
				float ah = 0.4F;
				float ai = 0.3F;
				if (af % 2 == 0) {
					ag *= 0.7F;
					ah *= 0.7F;
					ai *= 0.7F;
				}

				float aj = (float)af / 24.0F;
				bufferBuilder.vertex(
						d + v * (double)aj + 0.0, e + w * (double)(aj * aj + aj) * 0.5 + (double)((24.0F - (float)af) / 18.0F + 0.125F) + 0.025, f + x * (double)aj
					)
					.color(ag, ah, ai, 1.0F)
					.next();
				bufferBuilder.vertex(
						d + v * (double)aj + 0.025, e + w * (double)(aj * aj + aj) * 0.5 + (double)((24.0F - (float)af) / 18.0F + 0.125F), f + x * (double)aj + 0.025
					)
					.color(ag, ah, ai, 1.0F)
					.next();
			}

			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			GlStateManager.enableCull();
		}
	}
}
