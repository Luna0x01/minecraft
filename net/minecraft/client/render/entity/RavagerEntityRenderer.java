package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.RavagerEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

public class RavagerEntityRenderer extends MobEntityRenderer<RavagerEntity, RavagerEntityModel> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/ravager.png");

	public RavagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new RavagerEntityModel(), 1.1F);
	}

	public Identifier getTexture(RavagerEntity ravagerEntity) {
		return TEXTURE;
	}
}
