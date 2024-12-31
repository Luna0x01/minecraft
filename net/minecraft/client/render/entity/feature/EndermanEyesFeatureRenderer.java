package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
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
		GlStateManager.method_12287(GlStateManager.class_2870.ONE, GlStateManager.class_2866.ONE);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(!endermanEntity.isInvisible());
		int m = 61680;
		int n = 61680;
		int o = 0;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, 61680.0F, 0.0F);
		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().gameRenderer.method_13847(true);
		this.endermanRenderer.getModel().render(endermanEntity, f, g, i, j, k, l);
		MinecraftClient.getInstance().gameRenderer.method_13847(false);
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
