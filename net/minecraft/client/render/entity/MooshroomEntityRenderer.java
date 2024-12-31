package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;

public class MooshroomEntityRenderer extends MobEntityRenderer<MooshroomEntity> {
	private static final Identifier MOOSHROOM_TEX = new Identifier("textures/entity/cow/mooshroom.png");

	public MooshroomEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
		this.addFeature(new MooshroomMushroomFeatureRenderer(this));
	}

	protected Identifier getTexture(MooshroomEntity mooshroomEntity) {
		return MOOSHROOM_TEX;
	}
}
