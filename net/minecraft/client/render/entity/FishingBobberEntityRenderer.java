package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FishingBobberEntityRenderer extends EntityRenderer<FishingBobberEntity> {
	private static final Identifier PARTICLES = new Identifier("textures/particle/particles.png");

	public FishingBobberEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(FishingBobberEntity fishingBobberEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d, (float)e, (float)f);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		this.bindTexture(fishingBobberEntity);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		int i = 1;
		int j = 2;
		float k = 0.0625F;
		float l = 0.125F;
		float m = 0.125F;
		float n = 0.1875F;
		float o = 1.0F;
		float p = 0.5F;
		float q = 0.5F;
		GlStateManager.rotate(180.0F - this.dispatcher.yaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-this.dispatcher.pitch, 1.0F, 0.0F, 0.0F);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_NORMAL);
		bufferBuilder.vertex(-0.5, -0.5, 0.0).texture(0.0625, 0.1875).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(0.5, -0.5, 0.0).texture(0.125, 0.1875).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(0.5, 0.5, 0.0).texture(0.125, 0.125).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(-0.5, 0.5, 0.0).texture(0.0625, 0.125).normal(0.0F, 1.0F, 0.0F).next();
		tessellator.draw();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		if (fishingBobberEntity.thrower != null) {
			float r = fishingBobberEntity.thrower.getHandSwingProgress(h);
			float s = MathHelper.sin(MathHelper.sqrt(r) * (float) Math.PI);
			Vec3d vec3d = new Vec3d(-0.36, 0.03, 0.35);
			vec3d = vec3d.rotateX(
				-(fishingBobberEntity.thrower.prevPitch + (fishingBobberEntity.thrower.pitch - fishingBobberEntity.thrower.prevPitch) * h) * (float) Math.PI / 180.0F
			);
			vec3d = vec3d.rotateY(
				-(fishingBobberEntity.thrower.prevYaw + (fishingBobberEntity.thrower.yaw - fishingBobberEntity.thrower.prevYaw) * h) * (float) Math.PI / 180.0F
			);
			vec3d = vec3d.rotateY(s * 0.5F);
			vec3d = vec3d.rotateX(-s * 0.7F);
			double t = fishingBobberEntity.thrower.prevX + (fishingBobberEntity.thrower.x - fishingBobberEntity.thrower.prevX) * (double)h + vec3d.x;
			double u = fishingBobberEntity.thrower.prevY + (fishingBobberEntity.thrower.y - fishingBobberEntity.thrower.prevY) * (double)h + vec3d.y;
			double v = fishingBobberEntity.thrower.prevZ + (fishingBobberEntity.thrower.z - fishingBobberEntity.thrower.prevZ) * (double)h + vec3d.z;
			double w = (double)fishingBobberEntity.thrower.getEyeHeight();
			if (this.dispatcher.options != null && this.dispatcher.options.perspective > 0 || fishingBobberEntity.thrower != MinecraftClient.getInstance().player) {
				float x = (fishingBobberEntity.thrower.prevBodyYaw + (fishingBobberEntity.thrower.bodyYaw - fishingBobberEntity.thrower.prevBodyYaw) * h)
					* (float) Math.PI
					/ 180.0F;
				double y = (double)MathHelper.sin(x);
				double z = (double)MathHelper.cos(x);
				double aa = 0.35;
				double ab = 0.8;
				t = fishingBobberEntity.thrower.prevX + (fishingBobberEntity.thrower.x - fishingBobberEntity.thrower.prevX) * (double)h - z * 0.35 - y * 0.8;
				u = fishingBobberEntity.thrower.prevY + w + (fishingBobberEntity.thrower.y - fishingBobberEntity.thrower.prevY) * (double)h - 0.45;
				v = fishingBobberEntity.thrower.prevZ + (fishingBobberEntity.thrower.z - fishingBobberEntity.thrower.prevZ) * (double)h - y * 0.35 + z * 0.8;
				w = fishingBobberEntity.thrower.isSneaking() ? -0.1875 : 0.0;
			}

			double ac = fishingBobberEntity.prevX + (fishingBobberEntity.x - fishingBobberEntity.prevX) * (double)h;
			double ad = fishingBobberEntity.prevY + (fishingBobberEntity.y - fishingBobberEntity.prevY) * (double)h + 0.25;
			double ae = fishingBobberEntity.prevZ + (fishingBobberEntity.z - fishingBobberEntity.prevZ) * (double)h;
			double af = (double)((float)(t - ac));
			double ag = (double)((float)(u - ad)) + w;
			double ah = (double)((float)(v - ae));
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
			int ai = 16;

			for (int aj = 0; aj <= 16; aj++) {
				float ak = (float)aj / 16.0F;
				bufferBuilder.vertex(d + af * (double)ak, e + ag * (double)(ak * ak + ak) * 0.5 + 0.25, f + ah * (double)ak).color(0, 0, 0, 255).next();
			}

			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			super.render(fishingBobberEntity, d, e, f, g, h);
		}
	}

	protected Identifier getTexture(FishingBobberEntity fishingBobberEntity) {
		return PARTICLES;
	}
}
