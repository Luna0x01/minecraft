package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.WitherEntityRenderer;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WitherArmorFeatureRenderer implements FeatureRenderer<WitherEntity> {
	private static final Identifier WITHER_REGEN_TEXTURE = new Identifier("textures/entity/wither/wither_armor.png");
	private final WitherEntityRenderer witherEntityRenderer;
	private final WitherEntityModel model = new WitherEntityModel(0.5F);

	public WitherArmorFeatureRenderer(WitherEntityRenderer witherEntityRenderer) {
		this.witherEntityRenderer = witherEntityRenderer;
	}

	public void render(WitherEntity witherEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (witherEntity.shouldRenderOverlay()) {
			GlStateManager.depthMask(!witherEntity.isInvisible());
			this.witherEntityRenderer.bindTexture(WITHER_REGEN_TEXTURE);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float m = (float)witherEntity.ticksAlive + h;
			float n = MathHelper.cos(m * 0.02F) * 3.0F;
			float o = m * 0.01F;
			GlStateManager.translate(n, o, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float p = 0.5F;
			GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.method_12287(GlStateManager.class_2870.ONE, GlStateManager.class_2866.ONE);
			this.model.animateModel(witherEntity, f, g, h);
			this.model.copy(this.witherEntityRenderer.getModel());
			this.model.render(witherEntity, f, g, i, j, k, l);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
