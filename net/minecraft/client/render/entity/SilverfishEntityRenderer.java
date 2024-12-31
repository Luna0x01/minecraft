package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.SilverfishEntityModel;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.util.Identifier;

public class SilverfishEntityRenderer extends MobEntityRenderer<SilverfishEntity> {
	private static final Identifier SILVERFISH_TEX = new Identifier("textures/entity/silverfish.png");

	public SilverfishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SilverfishEntityModel(), 0.3F);
	}

	protected float method_5771(SilverfishEntity silverfishEntity) {
		return 180.0F;
	}

	protected Identifier getTexture(SilverfishEntity silverfishEntity) {
		return SILVERFISH_TEX;
	}
}
