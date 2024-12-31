package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

public class StrayEntityRenderer extends SkeletonEntityRenderer {
	private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/stray.png");

	public StrayEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.addFeature(new StrayOverlayFeatureRenderer<>(this));
	}

	@Override
	public Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
		return TEXTURE;
	}
}
