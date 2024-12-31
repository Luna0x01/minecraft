package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;

public class BlazeEntityRenderer extends MobEntityRenderer<BlazeEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/blaze.png");

	public BlazeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new BlazeEntityModel(), 0.5F);
	}

	protected Identifier getTexture(BlazeEntity blazeEntity) {
		return TEXTURE;
	}
}
