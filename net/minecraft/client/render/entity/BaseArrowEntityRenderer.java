package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.math.MathHelper;

public abstract class BaseArrowEntityRenderer<T extends AbstractArrowEntity> extends EntityRenderer<T> {
	public BaseArrowEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(T abstractArrowEntity, double d, double e, double f, float g, float h) {
		this.bindTexture(abstractArrowEntity);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate((float)d, (float)e, (float)f);
		GlStateManager.rotate(abstractArrowEntity.prevYaw + (abstractArrowEntity.yaw - abstractArrowEntity.prevYaw) * h - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(abstractArrowEntity.prevPitch + (abstractArrowEntity.pitch - abstractArrowEntity.prevPitch) * h, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		int i = 0;
		float j = 0.0F;
		float k = 0.5F;
		float l = 0.0F;
		float m = 0.15625F;
		float n = 0.0F;
		float o = 0.15625F;
		float p = 0.15625F;
		float q = 0.3125F;
		float r = 0.05625F;
		GlStateManager.enableRescaleNormal();
		float s = (float)abstractArrowEntity.shake - h;
		if (s > 0.0F) {
			float t = -MathHelper.sin(s * 3.0F) * s;
			GlStateManager.rotate(t, 0.0F, 0.0F, 1.0F);
		}

		GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(0.05625F, 0.05625F, 0.05625F);
		GlStateManager.translate(-4.0F, 0.0F, 0.0F);
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(abstractArrowEntity));
		}

		GlStateManager.method_12272(0.05625F, 0.0F, 0.0F);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-7.0, -2.0, -2.0).texture(0.0, 0.15625).next();
		bufferBuilder.vertex(-7.0, -2.0, 2.0).texture(0.15625, 0.15625).next();
		bufferBuilder.vertex(-7.0, 2.0, 2.0).texture(0.15625, 0.3125).next();
		bufferBuilder.vertex(-7.0, 2.0, -2.0).texture(0.0, 0.3125).next();
		tessellator.draw();
		GlStateManager.method_12272(-0.05625F, 0.0F, 0.0F);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-7.0, 2.0, -2.0).texture(0.0, 0.15625).next();
		bufferBuilder.vertex(-7.0, 2.0, 2.0).texture(0.15625, 0.15625).next();
		bufferBuilder.vertex(-7.0, -2.0, 2.0).texture(0.15625, 0.3125).next();
		bufferBuilder.vertex(-7.0, -2.0, -2.0).texture(0.0, 0.3125).next();
		tessellator.draw();

		for (int u = 0; u < 4; u++) {
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.method_12272(0.0F, 0.0F, 0.05625F);
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex(-8.0, -2.0, 0.0).texture(0.0, 0.0).next();
			bufferBuilder.vertex(8.0, -2.0, 0.0).texture(0.5, 0.0).next();
			bufferBuilder.vertex(8.0, 2.0, 0.0).texture(0.5, 0.15625).next();
			bufferBuilder.vertex(-8.0, 2.0, 0.0).texture(0.0, 0.15625).next();
			tessellator.draw();
		}

		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
		super.render(abstractArrowEntity, d, e, f, g, h);
	}
}
