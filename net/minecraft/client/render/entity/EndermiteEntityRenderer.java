package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.EndermiteEntityModel;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.util.Identifier;

public class EndermiteEntityRenderer extends MobEntityRenderer<EndermiteEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/endermite.png");

	public EndermiteEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EndermiteEntityModel(), 0.3F);
	}

	protected float method_5771(EndermiteEntity endermiteEntity) {
		return 180.0F;
	}

	protected Identifier getTexture(EndermiteEntity endermiteEntity) {
		return TEXTURE;
	}
}
