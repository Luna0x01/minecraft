package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SilverfishEntityModel;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.util.Identifier;

public class SilverfishEntityRenderer extends MobEntityRenderer<SilverfishEntity, SilverfishEntityModel<SilverfishEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/silverfish.png");

	public SilverfishEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SilverfishEntityModel<>(context.getPart(EntityModelLayers.SILVERFISH)), 0.3F);
	}

	protected float getLyingAngle(SilverfishEntity silverfishEntity) {
		return 180.0F;
	}

	public Identifier getTexture(SilverfishEntity silverfishEntity) {
		return TEXTURE;
	}
}
