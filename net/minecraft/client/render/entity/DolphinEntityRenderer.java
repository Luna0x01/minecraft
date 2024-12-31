package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

public class DolphinEntityRenderer extends MobEntityRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/dolphin.png");

	public DolphinEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new DolphinEntityModel<>(context.getPart(EntityModelLayers.DOLPHIN)), 0.7F);
		this.addFeature(new DolphinHeldItemFeatureRenderer(this));
	}

	public Identifier getTexture(DolphinEntity dolphinEntity) {
		return TEXTURE;
	}
}
