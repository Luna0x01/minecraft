package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;

public class CowEntityRenderer extends MobEntityRenderer<CowEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/cow/cow.png");

	public CowEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
	}

	protected Identifier getTexture(CowEntity cowEntity) {
		return TEXTURE;
	}
}
