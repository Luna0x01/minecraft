package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class BaseArrowEntityRenderer extends EntityRenderer<AbstractArrowEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/arrow.png");

	public BaseArrowEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(AbstractArrowEntity abstractArrowEntity, double d, double e, double f, float g, float h) {
		this.bindTexture(abstractArrowEntity);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d, (float)e, (float)f);
		GlStateManager.rotate(abstractArrowEntity.prevYaw + (abstractArrowEntity.yaw - abstractArrowEntity.prevYaw) * h - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(abstractArrowEntity.prevPitch + (abstractArrowEntity.pitch - abstractArrowEntity.prevPitch) * h, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		int i = 0;
		float j = 0.0F;
		float k = 0.5F;
		float l = (float)(0 + i * 10) / 32.0F;
		float m = (float)(5 + i * 10) / 32.0F;
		float n = 0.0F;
		float o = 0.15625F;
		float p = (float)(5 + i * 10) / 32.0F;
		float q = (float)(10 + i * 10) / 32.0F;
		float r = 0.05625F;
		GlStateManager.enableRescaleNormal();
		float s = (float)abstractArrowEntity.shake - h;
		if (s > 0.0F) {
			float t = -MathHelper.sin(s * 3.0F) * s;
			GlStateManager.rotate(t, 0.0F, 0.0F, 1.0F);
		}

		GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(r, r, r);
		GlStateManager.translate(-4.0F, 0.0F, 0.0F);
		GL11.glNormal3f(r, 0.0F, 0.0F);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-7.0, -2.0, -2.0).texture((double)n, (double)p).next();
		bufferBuilder.vertex(-7.0, -2.0, 2.0).texture((double)o, (double)p).next();
		bufferBuilder.vertex(-7.0, 2.0, 2.0).texture((double)o, (double)q).next();
		bufferBuilder.vertex(-7.0, 2.0, -2.0).texture((double)n, (double)q).next();
		tessellator.draw();
		GL11.glNormal3f(-r, 0.0F, 0.0F);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-7.0, 2.0, -2.0).texture((double)n, (double)p).next();
		bufferBuilder.vertex(-7.0, 2.0, 2.0).texture((double)o, (double)p).next();
		bufferBuilder.vertex(-7.0, -2.0, 2.0).texture((double)o, (double)q).next();
		bufferBuilder.vertex(-7.0, -2.0, -2.0).texture((double)n, (double)q).next();
		tessellator.draw();

		for (int u = 0; u < 4; u++) {
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glNormal3f(0.0F, 0.0F, r);
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex(-8.0, -2.0, 0.0).texture((double)j, (double)l).next();
			bufferBuilder.vertex(8.0, -2.0, 0.0).texture((double)k, (double)l).next();
			bufferBuilder.vertex(8.0, 2.0, 0.0).texture((double)k, (double)m).next();
			bufferBuilder.vertex(-8.0, 2.0, 0.0).texture((double)j, (double)m).next();
			tessellator.draw();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.render(abstractArrowEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(AbstractArrowEntity abstractArrowEntity) {
		return TEXTURE;
	}
}
