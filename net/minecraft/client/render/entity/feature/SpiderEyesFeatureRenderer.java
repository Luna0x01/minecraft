package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

public class SpiderEyesFeatureRenderer implements FeatureRenderer<SpiderEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/spider_eyes.png");
	private final SpiderEntityRenderer spiderRenderer;

	public SpiderEyesFeatureRenderer(SpiderEntityRenderer spiderEntityRenderer) {
		this.spiderRenderer = spiderEntityRenderer;
	}

	public void render(SpiderEntity spiderEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.spiderRenderer.bindTexture(TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(1, 1);
		if (spiderEntity.isInvisible()) {
			GlStateManager.depthMask(false);
		} else {
			GlStateManager.depthMask(true);
		}

		int m = 61680;
		int n = m % 65536;
		int o = m / 65536;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)n / 1.0F, (float)o / 1.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.spiderRenderer.getModel().render(spiderEntity, f, g, i, j, k, l);
		m = spiderEntity.getLightmapCoordinates(h);
		n = m % 65536;
		o = m / 65536;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)n / 1.0F, (float)o / 1.0F);
		this.spiderRenderer.method_10261(spiderEntity, h);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
