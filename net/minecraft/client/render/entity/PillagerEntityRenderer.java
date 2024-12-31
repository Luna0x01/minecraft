package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PillagerEntityModel;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

public class PillagerEntityRenderer extends IllagerEntityRenderer<PillagerEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/illager/pillager.png");

	public PillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PillagerEntityModel<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.addFeature(new HeldItemFeatureRenderer<>(this));
	}

	protected Identifier method_4092(PillagerEntity pillagerEntity) {
		return SKIN;
	}
}
