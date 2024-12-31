package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.StrayEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

public class SkeletonEntityRenderer extends BipedEntityRenderer<AbstractSkeletonEntity, StrayEntityModel<AbstractSkeletonEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/skeleton/skeleton.png");

	public SkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new StrayEntityModel<>(), 0.5F);
		this.addFeature(new HeldItemFeatureRenderer<>(this));
		this.addFeature(new ArmorBipedFeatureRenderer<>(this, new StrayEntityModel(0.5F, true), new StrayEntityModel(1.0F, true)));
	}

	protected Identifier method_4119(AbstractSkeletonEntity abstractSkeletonEntity) {
		return SKIN;
	}
}
