package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.util.Identifier;

public class GoatEntityRenderer extends MobEntityRenderer<GoatEntity, GoatEntityModel<GoatEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/goat/goat.png");

	public GoatEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new GoatEntityModel<>(context.getPart(EntityModelLayers.GOAT)), 0.7F);
	}

	public Identifier getTexture(GoatEntity goatEntity) {
		return TEXTURE;
	}
}
