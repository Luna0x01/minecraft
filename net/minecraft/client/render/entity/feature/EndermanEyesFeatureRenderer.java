package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;

public class EndermanEyesFeatureRenderer implements FeatureRenderer<EndermanEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/enderman/enderman_eyes.png");
	private final EndermanEntityRenderer endermanRenderer;

	public EndermanEyesFeatureRenderer(EndermanEntityRenderer endermanEntityRenderer) {
		this.endermanRenderer = endermanEntityRenderer;
	}

	public void render(EndermanEntity endermanEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.endermanRenderer.bindTexture(TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(1, 1);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(!endermanEntity.isInvisible());
		int m = 61680;
		int n = m % 65536;
		int o = m / 65536;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)n / 1.0F, (float)o / 1.0F);
		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.endermanRenderer.getModel().render(endermanEntity, f, g, i, j, k, l);
		this.endermanRenderer.method_10261(endermanEntity, h);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
