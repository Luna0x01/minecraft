package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

public class CreeperLightningFeatureRenderer implements FeatureRenderer<CreeperEntity> {
	private static final Identifier lightningArmour = new Identifier("textures/entity/creeper/creeper_armor.png");
	private final CreeperEntityRenderer renderer;
	private final CreeperEntityModel model = new CreeperEntityModel(2.0F);

	public CreeperLightningFeatureRenderer(CreeperEntityRenderer creeperEntityRenderer) {
		this.renderer = creeperEntityRenderer;
	}

	public void render(CreeperEntity creeperEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (creeperEntity.method_3074()) {
			boolean bl = creeperEntity.isInvisible();
			GlStateManager.depthMask(!bl);
			this.renderer.bindTexture(lightningArmour);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float m = (float)creeperEntity.ticksAlive + h;
			GlStateManager.translate(m * 0.01F, m * 0.01F, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float n = 0.5F;
			GlStateManager.color(n, n, n, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(1, 1);
			this.model.copy(this.renderer.getModel());
			this.model.render(creeperEntity, f, g, i, j, k, l);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(bl);
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
