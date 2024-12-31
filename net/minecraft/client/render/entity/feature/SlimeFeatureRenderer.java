package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.mob.SlimeEntity;

public class SlimeFeatureRenderer implements FeatureRenderer<SlimeEntity> {
	private final SlimeEntityRenderer renderer;
	private final EntityModel model = new SlimeEntityModel(0);

	public SlimeFeatureRenderer(SlimeEntityRenderer slimeEntityRenderer) {
		this.renderer = slimeEntityRenderer;
	}

	public void render(SlimeEntity slimeEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!slimeEntity.isInvisible()) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
			this.model.copy(this.renderer.getModel());
			this.model.render(slimeEntity, f, g, i, j, k, l);
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
		}
	}

	@Override
	public boolean combineTextures() {
		return true;
	}
}
