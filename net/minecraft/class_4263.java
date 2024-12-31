package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;

public class class_4263 extends EntityRenderer<TridentEntity> {
	public static final Identifier field_20946 = new Identifier("textures/entity/trident.png");
	private final class_4197 field_20947 = new class_4197();

	public class_4263(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(TridentEntity tridentEntity, double d, double e, double f, float g, float h) {
		this.bindTexture(tridentEntity);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate((float)d, (float)e, (float)f);
		GlStateManager.rotate(tridentEntity.prevYaw + (tridentEntity.yaw - tridentEntity.prevYaw) * h - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(tridentEntity.prevPitch + (tridentEntity.pitch - tridentEntity.prevPitch) * h + 90.0F, 0.0F, 0.0F, 1.0F);
		this.field_20947.method_18943();
		GlStateManager.popMatrix();
		this.method_19421(tridentEntity, d, e, f, g, h);
		super.render(tridentEntity, d, e, f, g, h);
		GlStateManager.enableLighting();
	}

	protected Identifier getTexture(TridentEntity tridentEntity) {
		return field_20946;
	}

	private double method_19418(double d, double e, double f) {
		return d + (e - d) * f;
	}

	protected void method_19421(TridentEntity tridentEntity, double d, double e, double f, float g, float h) {
		Entity entity = tridentEntity.method_15950();
		if (entity != null && tridentEntity.method_15953()) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			double i = this.method_19418((double)entity.prevYaw, (double)entity.yaw, (double)(h * 0.5F)) * (float) (Math.PI / 180.0);
			double j = Math.cos(i);
			double k = Math.sin(i);
			double l = this.method_19418(entity.prevX, entity.x, (double)h);
			double m = this.method_19418(entity.prevY + (double)entity.getEyeHeight() * 0.8, entity.y + (double)entity.getEyeHeight() * 0.8, (double)h);
			double n = this.method_19418(entity.prevZ, entity.z, (double)h);
			double o = j - k;
			double p = k + j;
			double q = this.method_19418(tridentEntity.prevX, tridentEntity.x, (double)h);
			double r = this.method_19418(tridentEntity.prevY, tridentEntity.y, (double)h);
			double s = this.method_19418(tridentEntity.prevZ, tridentEntity.z, (double)h);
			double t = (double)((float)(l - q));
			double u = (double)((float)(m - r));
			double v = (double)((float)(n - s));
			double w = Math.sqrt(t * t + u * u + v * v);
			int x = tridentEntity.getEntityId() + tridentEntity.ticksAlive;
			double y = (double)((float)x + h) * -0.1;
			double z = Math.min(0.5, w / 30.0);
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, 255.0F, 255.0F);
			bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
			int aa = 37;
			int ab = 7 - x % 7;
			double ac = 0.1;

			for (int ad = 0; ad <= 37; ad++) {
				double ae = (double)ad / 37.0;
				float af = 1.0F - (float)((ad + ab) % 7) / 7.0F;
				double ag = ae * 2.0 - 1.0;
				ag = (1.0 - ag * ag) * z;
				double ah = d + t * ae + Math.sin(ae * Math.PI * 8.0 + y) * o * ag;
				double ai = e + u * ae + Math.cos(ae * Math.PI * 8.0 + y) * 0.02 + (0.1 + ag) * 1.0;
				double aj = f + v * ae + Math.sin(ae * Math.PI * 8.0 + y) * p * ag;
				float ak = 0.87F * af + 0.3F * (1.0F - af);
				float al = 0.91F * af + 0.6F * (1.0F - af);
				float am = 0.85F * af + 0.5F * (1.0F - af);
				bufferBuilder.vertex(ah, ai, aj).color(ak, al, am, 1.0F).next();
				bufferBuilder.vertex(ah + 0.1 * ag, ai + 0.1 * ag, aj).color(ak, al, am, 1.0F).next();
				if (ad > tridentEntity.field_17105 * 2) {
					break;
				}
			}

			tessellator.draw();
			bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

			for (int an = 0; an <= 37; an++) {
				double ao = (double)an / 37.0;
				float ap = 1.0F - (float)((an + ab) % 7) / 7.0F;
				double aq = ao * 2.0 - 1.0;
				aq = (1.0 - aq * aq) * z;
				double ar = d + t * ao + Math.sin(ao * Math.PI * 8.0 + y) * o * aq;
				double as = e + u * ao + Math.cos(ao * Math.PI * 8.0 + y) * 0.01 + (0.1 + aq) * 1.0;
				double at = f + v * ao + Math.sin(ao * Math.PI * 8.0 + y) * p * aq;
				float au = 0.87F * ap + 0.3F * (1.0F - ap);
				float av = 0.91F * ap + 0.6F * (1.0F - ap);
				float aw = 0.85F * ap + 0.5F * (1.0F - ap);
				bufferBuilder.vertex(ar, as, at).color(au, av, aw, 1.0F).next();
				bufferBuilder.vertex(ar + 0.1 * aq, as, at + 0.1 * aq).color(au, av, aw, 1.0F).next();
				if (an > tridentEntity.field_17105 * 2) {
					break;
				}
			}

			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			GlStateManager.enableCull();
		}
	}
}
