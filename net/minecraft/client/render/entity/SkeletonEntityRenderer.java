package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

public class SkeletonEntityRenderer extends BipedEntityRenderer<AbstractSkeletonEntity, SkeletonEntityModel<AbstractSkeletonEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");

	public SkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SkeletonEntityModel<>(), 0.5F);
		this.addFeature(new ArmorFeatureRenderer<>(this, new SkeletonEntityModel(0.5F, true), new SkeletonEntityModel(1.0F, true)));
	}

	public Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
		return TEXTURE;
	}
}
