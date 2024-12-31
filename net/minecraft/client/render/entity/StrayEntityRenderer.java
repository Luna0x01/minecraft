package net.minecraft.client.render.entity;

import net.minecraft.class_3146;
import net.minecraft.client.render.entity.feature.StrayFeatureRenderer;
import net.minecraft.util.Identifier;

public class StrayEntityRenderer extends SkeletonEntityRenderer {
	private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/stray.png");

	public StrayEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.addFeature(new StrayFeatureRenderer(this));
	}

	@Override
	protected Identifier getTexture(class_3146 arg) {
		return TEXTURE;
	}
}
