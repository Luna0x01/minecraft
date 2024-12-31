package net.minecraft.client.render.entity;

import net.minecraft.class_3146;
import net.minecraft.client.render.entity.feature.ArmorRenderer;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.util.Identifier;

public class SkeletonEntityRenderer extends BipedEntityRenderer<class_3146> {
	private static final Identifier SKELETON_TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");

	public SkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SkeletonEntityModel(), 0.5F);
		this.addFeature(new HeldItemRenderer(this));
		this.addFeature(new ArmorRenderer(this) {
			@Override
			protected void init() {
				this.secondLayer = new SkeletonEntityModel(0.5F, true);
				this.firstLayer = new SkeletonEntityModel(1.0F, true);
			}
		});
	}

	protected Identifier getTexture(class_3146 arg) {
		return SKELETON_TEXTURE;
	}
}
