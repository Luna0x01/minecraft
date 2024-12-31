package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ExperienceOrbEntityRenderer extends EntityRenderer<ExperienceOrbEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/experience_orb.png");

	public ExperienceOrbEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.shadowSize = 0.15F;
		this.shadowDarkness = 0.75F;
	}

	public void render(ExperienceOrbEntity experienceOrbEntity, double d, double e, double f, float g, float h) {
		if (!this.field_13631) {
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)d, (float)e, (float)f);
			this.bindTexture(experienceOrbEntity);
			DiffuseLighting.enableNormally();
			int i = experienceOrbEntity.getOrbSize();
			float j = (float)(i % 4 * 16 + 0) / 64.0F;
			float k = (float)(i % 4 * 16 + 16) / 64.0F;
			float l = (float)(i / 4 * 16 + 0) / 64.0F;
			float m = (float)(i / 4 * 16 + 16) / 64.0F;
			float n = 1.0F;
			float o = 0.5F;
			float p = 0.25F;
			int q = experienceOrbEntity.getLightmapCoordinates(h);
			int r = q % 65536;
			int s = q / 65536;
			GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)r, (float)s);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			float t = 255.0F;
			float u = ((float)experienceOrbEntity.renderTicks + h) / 2.0F;
			s = (int)((MathHelper.sin(u + 0.0F) + 1.0F) * 0.5F * 255.0F);
			int w = 255;
			int x = (int)((MathHelper.sin(u + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
			GlStateManager.translate(0.0F, 0.1F, 0.0F);
			GlStateManager.rotate(180.0F - this.dispatcher.yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((float)(this.dispatcher.options.perspective == 2 ? -1 : 1) * -this.dispatcher.pitch, 1.0F, 0.0F, 0.0F);
			float y = 0.3F;
			GlStateManager.scale(0.3F, 0.3F, 0.3F);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
			bufferBuilder.vertex(-0.5, -0.25, 0.0).texture((double)j, (double)m).color(s, 255, x, 128).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.5, -0.25, 0.0).texture((double)k, (double)m).color(s, 255, x, 128).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(0.5, 0.75, 0.0).texture((double)k, (double)l).color(s, 255, x, 128).normal(0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(-0.5, 0.75, 0.0).texture((double)j, (double)l).color(s, 255, x, 128).normal(0.0F, 1.0F, 0.0F).next();
			tessellator.draw();
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			super.render(experienceOrbEntity, d, e, f, g, h);
		}
	}

	protected Identifier getTexture(ExperienceOrbEntity experienceOrbEntity) {
		return TEXTURE;
	}
}
