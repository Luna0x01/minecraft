package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;

public class DragonEyesFeatureRenderer implements FeatureRenderer<EnderDragonEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/enderdragon/dragon_eyes.png");
	private final EnderDragonEntityRenderer dragonRenderer;

	public DragonEyesFeatureRenderer(EnderDragonEntityRenderer enderDragonEntityRenderer) {
		this.dragonRenderer = enderDragonEntityRenderer;
	}

	public void render(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.dragonRenderer.bindTexture(TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.method_12287(GlStateManager.class_2870.ONE, GlStateManager.class_2866.ONE);
		GlStateManager.disableLighting();
		GlStateManager.depthFunc(514);
		int m = 61680;
		int n = 61680;
		int o = 0;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, 61680.0F, 0.0F);
		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.dragonRenderer.getModel().render(enderDragonEntity, f, g, i, j, k, l);
		this.dragonRenderer.method_10261(enderDragonEntity, h);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.depthFunc(515);
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
