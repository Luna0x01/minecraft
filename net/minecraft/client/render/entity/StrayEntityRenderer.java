package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

public class StrayEntityRenderer extends SkeletonEntityRenderer {
	private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/stray.png");

	public StrayEntityRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.STRAY, EntityModelLayers.STRAY_INNER_ARMOR, EntityModelLayers.STRAY_OUTER_ARMOR);
		this.addFeature(new StrayOverlayFeatureRenderer<>(this, context.getModelLoader()));
	}

	@Override
	public Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
		return TEXTURE;
	}
}
