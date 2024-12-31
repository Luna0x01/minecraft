package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;

public class class_4270 implements FeatureRenderer<PhantomEntity> {
	private static final Identifier field_20963 = new Identifier("textures/entity/phantom_eyes.png");
	private final class_4259 field_20964;

	public class_4270(class_4259 arg) {
		this.field_20964 = arg;
	}

	public void render(PhantomEntity phantomEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.field_20964.bindTexture(field_20963);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.method_12287(GlStateManager.class_2870.ONE, GlStateManager.class_2866.ONE);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(!phantomEntity.isInvisible());
		int m = 61680;
		int n = 61680;
		int o = 0;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, 61680.0F, 0.0F);
		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().field_3818.method_19079(true);
		this.field_20964.getModel().render(phantomEntity, f, g, i, j, k, l);
		MinecraftClient.getInstance().field_3818.method_19079(false);
		this.field_20964.method_14692(phantomEntity);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
