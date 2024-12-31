package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.CameraView;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class GuardianEntityRenderer extends MobEntityRenderer<GuardianEntity> {
	private static final Identifier GUARDIAN_TEX = new Identifier("textures/entity/guardian.png");
	private static final Identifier ELDER_GUARDIAN_TEX = new Identifier("textures/entity/guardian_elder.png");
	private static final Identifier GUARDIAN_BEAM = new Identifier("textures/entity/guardian_beam.png");
	int field_11104 = ((GuardianEntityModel)this.model).method_9635();

	public GuardianEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new GuardianEntityModel(), 0.5F);
	}

	public boolean shouldRender(GuardianEntity guardianEntity, CameraView cameraView, double d, double e, double f) {
		if (super.shouldRender(guardianEntity, cameraView, d, e, f)) {
			return true;
		} else {
			if (guardianEntity.hasBeamTarget()) {
				LivingEntity livingEntity = guardianEntity.getBeamTarget();
				if (livingEntity != null) {
					Vec3d vec3d = this.method_10214(livingEntity, (double)livingEntity.height * 0.5, 1.0F);
					Vec3d vec3d2 = this.method_10214(guardianEntity, (double)guardianEntity.getEyeHeight(), 1.0F);
					if (cameraView.isBoxInFrustum(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z))) {
						return true;
					}
				}
			}

			return false;
		}
	}

	private Vec3d method_10214(LivingEntity livingEntity, double d, float f) {
		double e = livingEntity.prevTickX + (livingEntity.x - livingEntity.prevTickX) * (double)f;
		double g = d + livingEntity.prevTickY + (livingEntity.y - livingEntity.prevTickY) * (double)f;
		double h = livingEntity.prevTickZ + (livingEntity.z - livingEntity.prevTickZ) * (double)f;
		return new Vec3d(e, g, h);
	}

	public void render(GuardianEntity guardianEntity, double d, double e, double f, float g, float h) {
		if (this.field_11104 != ((GuardianEntityModel)this.model).method_9635()) {
			this.model = new GuardianEntityModel();
			this.field_11104 = ((GuardianEntityModel)this.model).method_9635();
		}

		super.render(guardianEntity, d, e, f, g, h);
		LivingEntity livingEntity = guardianEntity.getBeamTarget();
		if (livingEntity != null) {
			float i = guardianEntity.getBeamProgress(h);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			this.bindTexture(GUARDIAN_BEAM);
			GlStateManager.method_12294(3553, 10242, 10497);
			GlStateManager.method_12294(3553, 10243, 10497);
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			float j = 240.0F;
			GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, j, j);
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			float k = (float)guardianEntity.world.getLastUpdateTime() + h;
			float l = k * 0.5F % 1.0F;
			float m = guardianEntity.getEyeHeight();
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)d, (float)e + m, (float)f);
			Vec3d vec3d = this.method_10214(livingEntity, (double)livingEntity.height * 0.5, h);
			Vec3d vec3d2 = this.method_10214(guardianEntity, (double)m, h);
			Vec3d vec3d3 = vec3d.subtract(vec3d2);
			double n = vec3d3.length() + 1.0;
			vec3d3 = vec3d3.normalize();
			float o = (float)Math.acos(vec3d3.y);
			float p = (float)Math.atan2(vec3d3.z, vec3d3.x);
			GlStateManager.rotate(((float) (Math.PI / 2) + -p) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(o * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
			int q = 1;
			double r = (double)k * 0.05 * (1.0 - (double)(q & 1) * 2.5);
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			float s = i * i;
			int t = 64 + (int)(s * 191.0F);
			int u = 32 + (int)(s * 191.0F);
			int v = 128 - (int)(s * 64.0F);
			double w = (double)q * 0.2;
			double x = w * 1.41;
			double y = 0.0 + Math.cos(r + (Math.PI * 3.0 / 4.0)) * x;
			double z = 0.0 + Math.sin(r + (Math.PI * 3.0 / 4.0)) * x;
			double aa = 0.0 + Math.cos(r + (Math.PI / 4)) * x;
			double ab = 0.0 + Math.sin(r + (Math.PI / 4)) * x;
			double ac = 0.0 + Math.cos(r + (Math.PI * 5.0 / 4.0)) * x;
			double ad = 0.0 + Math.sin(r + (Math.PI * 5.0 / 4.0)) * x;
			double ae = 0.0 + Math.cos(r + (Math.PI * 7.0 / 4.0)) * x;
			double af = 0.0 + Math.sin(r + (Math.PI * 7.0 / 4.0)) * x;
			double ag = 0.0 + Math.cos(r + Math.PI) * w;
			double ah = 0.0 + Math.sin(r + Math.PI) * w;
			double ai = 0.0 + Math.cos(r + 0.0) * w;
			double aj = 0.0 + Math.sin(r + 0.0) * w;
			double ak = 0.0 + Math.cos(r + (Math.PI / 2)) * w;
			double al = 0.0 + Math.sin(r + (Math.PI / 2)) * w;
			double am = 0.0 + Math.cos(r + (Math.PI * 3.0 / 2.0)) * w;
			double an = 0.0 + Math.sin(r + (Math.PI * 3.0 / 2.0)) * w;
			double ap = 0.0;
			double aq = 0.4999;
			double ar = (double)(-1.0F + l);
			double as = n * (0.5 / w) + ar;
			bufferBuilder.vertex(ag, n, ah).texture(0.4999, as).color(t, u, v, 255).next();
			bufferBuilder.vertex(ag, 0.0, ah).texture(0.4999, ar).color(t, u, v, 255).next();
			bufferBuilder.vertex(ai, 0.0, aj).texture(0.0, ar).color(t, u, v, 255).next();
			bufferBuilder.vertex(ai, n, aj).texture(0.0, as).color(t, u, v, 255).next();
			bufferBuilder.vertex(ak, n, al).texture(0.4999, as).color(t, u, v, 255).next();
			bufferBuilder.vertex(ak, 0.0, al).texture(0.4999, ar).color(t, u, v, 255).next();
			bufferBuilder.vertex(am, 0.0, an).texture(0.0, ar).color(t, u, v, 255).next();
			bufferBuilder.vertex(am, n, an).texture(0.0, as).color(t, u, v, 255).next();
			double at = 0.0;
			if (guardianEntity.ticksAlive % 2 == 0) {
				at = 0.5;
			}

			bufferBuilder.vertex(y, n, z).texture(0.5, at + 0.5).color(t, u, v, 255).next();
			bufferBuilder.vertex(aa, n, ab).texture(1.0, at + 0.5).color(t, u, v, 255).next();
			bufferBuilder.vertex(ae, n, af).texture(1.0, at).color(t, u, v, 255).next();
			bufferBuilder.vertex(ac, n, ad).texture(0.5, at).color(t, u, v, 255).next();
			tessellator.draw();
			GlStateManager.popMatrix();
		}
	}

	protected void scale(GuardianEntity guardianEntity, float f) {
		if (guardianEntity.isElder()) {
			GlStateManager.scale(2.35F, 2.35F, 2.35F);
		}
	}

	protected Identifier getTexture(GuardianEntity guardianEntity) {
		return guardianEntity.isElder() ? ELDER_GUARDIAN_TEX : GUARDIAN_TEX;
	}
}
