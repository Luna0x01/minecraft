package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

public class PigSaddleFeatureRenderer implements FeatureRenderer<PigEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/pig/pig_saddle.png");
	private final PigEntityRenderer pigRenderer;
	private final PigEntityModel model = new PigEntityModel(0.5F);

	public PigSaddleFeatureRenderer(PigEntityRenderer pigEntityRenderer) {
		this.pigRenderer = pigEntityRenderer;
	}

	public void render(PigEntity pigEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (pigEntity.isSaddled()) {
			this.pigRenderer.bindTexture(TEXTURE);
			this.model.copy(this.pigRenderer.getModel());
			this.model.render(pigEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
