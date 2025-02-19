package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;

public class PhantomEyesFeatureRenderer<T extends PhantomEntity> extends EyesFeatureRenderer<T, PhantomEntityModel<T>> {
	private static final RenderLayer SKIN = RenderLayer.getEyes(new Identifier("textures/entity/phantom_eyes.png"));

	public PhantomEyesFeatureRenderer(FeatureRendererContext<T, PhantomEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public RenderLayer getEyesTexture() {
		return SKIN;
	}
}
