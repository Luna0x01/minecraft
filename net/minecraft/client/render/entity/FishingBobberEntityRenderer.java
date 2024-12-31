package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.HandOption;
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
		GlStateManager.rotate((float)(this.dispatcher.options.perspective == 2 ? -1 : 1) * -this.dispatcher.pitch, 1.0F, 0.0F, 0.0F);
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(fishingBobberEntity));
		}

		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_NORMAL);
		bufferBuilder.vertex(-0.5, -0.5, 0.0).texture(0.0625, 0.1875).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(0.5, -0.5, 0.0).texture(0.125, 0.1875).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(0.5, 0.5, 0.0).texture(0.125, 0.125).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(-0.5, 0.5, 0.0).texture(0.0625, 0.125).normal(0.0F, 1.0F, 0.0F).next();
		tessellator.draw();
		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		if (fishingBobberEntity.thrower != null && !this.field_13631) {
			int r = fishingBobberEntity.thrower.getDurability() == HandOption.RIGHT ? 1 : -1;
			float s = fishingBobberEntity.thrower.getHandSwingProgress(h);
			float t = MathHelper.sin(MathHelper.sqrt(s) * (float) Math.PI);
			float u = (fishingBobberEntity.thrower.prevBodyYaw + (fishingBobberEntity.thrower.bodyYaw - fishingBobberEntity.thrower.prevBodyYaw) * h)
				* (float) (Math.PI / 180.0);
			double v = (double)MathHelper.sin(u);
			double w = (double)MathHelper.cos(u);
			double x = (double)r * 0.35;
			double y = 0.8;
			double ad;
			double ae;
			double af;
			double ag;
			if ((this.dispatcher.options == null || this.dispatcher.options.perspective <= 0) && fishingBobberEntity.thrower == MinecraftClient.getInstance().player) {
				Vec3d vec3d = new Vec3d((double)r * -0.36, -0.05, 0.4);
				vec3d = vec3d.rotateX(
					-(fishingBobberEntity.thrower.prevPitch + (fishingBobberEntity.thrower.pitch - fishingBobberEntity.thrower.prevPitch) * h) * (float) (Math.PI / 180.0)
				);
				vec3d = vec3d.rotateY(
					-(fishingBobberEntity.thrower.prevYaw + (fishingBobberEntity.thrower.yaw - fishingBobberEntity.thrower.prevYaw) * h) * (float) (Math.PI / 180.0)
				);
				vec3d = vec3d.rotateY(t * 0.5F);
				vec3d = vec3d.rotateX(-t * 0.7F);
				ad = fishingBobberEntity.thrower.prevX + (fishingBobberEntity.thrower.x - fishingBobberEntity.thrower.prevX) * (double)h + vec3d.x;
				ae = fishingBobberEntity.thrower.prevY + (fishingBobberEntity.thrower.y - fishingBobberEntity.thrower.prevY) * (double)h + vec3d.y;
				af = fishingBobberEntity.thrower.prevZ + (fishingBobberEntity.thrower.z - fishingBobberEntity.thrower.prevZ) * (double)h + vec3d.z;
				ag = (double)fishingBobberEntity.thrower.getEyeHeight();
			} else {
				ad = fishingBobberEntity.thrower.prevX + (fishingBobberEntity.thrower.x - fishingBobberEntity.thrower.prevX) * (double)h - w * x - v * 0.8;
				ae = fishingBobberEntity.thrower.prevY
					+ (double)fishingBobberEntity.thrower.getEyeHeight()
					+ (fishingBobberEntity.thrower.y - fishingBobberEntity.thrower.prevY) * (double)h
					- 0.45;
				af = fishingBobberEntity.thrower.prevZ + (fishingBobberEntity.thrower.z - fishingBobberEntity.thrower.prevZ) * (double)h - v * x + w * 0.8;
				ag = fishingBobberEntity.thrower.isSneaking() ? -0.1875 : 0.0;
			}

			double ah = fishingBobberEntity.prevX + (fishingBobberEntity.x - fishingBobberEntity.prevX) * (double)h;
			double ai = fishingBobberEntity.prevY + (fishingBobberEntity.y - fishingBobberEntity.prevY) * (double)h + 0.25;
			double aj = fishingBobberEntity.prevZ + (fishingBobberEntity.z - fishingBobberEntity.prevZ) * (double)h;
			double ak = (double)((float)(ad - ah));
			double al = (double)((float)(ae - ai)) + ag;
			double am = (double)((float)(af - aj));
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
			int an = 16;

			for (int ao = 0; ao <= 16; ao++) {
				float ap = (float)ao / 16.0F;
				bufferBuilder.vertex(d + ak * (double)ap, e + al * (double)(ap * ap + ap) * 0.5 + 0.25, f + am * (double)ap).color(0, 0, 0, 255).next();
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
