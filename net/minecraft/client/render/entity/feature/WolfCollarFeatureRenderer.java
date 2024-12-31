package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

public class WolfCollarFeatureRenderer implements FeatureRenderer<WolfEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/wolf/wolf_collar.png");
	private final WolfEntityRenderer wolfRenderer;

	public WolfCollarFeatureRenderer(WolfEntityRenderer wolfEntityRenderer) {
		this.wolfRenderer = wolfEntityRenderer;
	}

	public void render(WolfEntity wolfEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (wolfEntity.isTamed() && !wolfEntity.isInvisible()) {
			this.wolfRenderer.bindTexture(TEXTURE);
			float[] fs = wolfEntity.getCollarColor().getColorComponents();
			GlStateManager.color(fs[0], fs[1], fs[2]);
			this.wolfRenderer.getModel().render(wolfEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean combineTextures() {
		return true;
	}
}
