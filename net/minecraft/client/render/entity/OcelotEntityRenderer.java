package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

public class OcelotEntityRenderer extends MobEntityRenderer<OcelotEntity, OcelotEntityModel<OcelotEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/cat/ocelot.png");

	public OcelotEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new OcelotEntityModel<>(context.getPart(EntityModelLayers.OCELOT)), 0.4F);
	}

	public Identifier getTexture(OcelotEntity ocelotEntity) {
		return TEXTURE;
	}
}
