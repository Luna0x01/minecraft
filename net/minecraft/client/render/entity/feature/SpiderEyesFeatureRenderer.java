package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class SpiderEyesFeatureRenderer<T extends Entity, M extends SpiderEntityModel<T>> extends EyesFeatureRenderer<T, M> {
	private static final RenderLayer SKIN = RenderLayer.getEyes(new Identifier("textures/entity/spider_eyes.png"));

	public SpiderEyesFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public RenderLayer getEyesTexture() {
		return SKIN;
	}
}
