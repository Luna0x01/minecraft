package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;

public class MooshroomEntityRenderer extends MobEntityRenderer<MooshroomEntity> {
	private static final Identifier MOOSHROOM_TEX = new Identifier("textures/entity/cow/mooshroom.png");

	public MooshroomEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CowEntityModel(), 0.7F);
		this.addFeature(new MooshroomMushroomFeatureRenderer(this));
	}

	public CowEntityModel getModel() {
		return (CowEntityModel)super.getModel();
	}

	protected Identifier getTexture(MooshroomEntity mooshroomEntity) {
		return MOOSHROOM_TEX;
	}
}
