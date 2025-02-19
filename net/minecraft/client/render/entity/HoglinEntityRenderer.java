package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.util.Identifier;

public class HoglinEntityRenderer extends MobEntityRenderer<HoglinEntity, HoglinEntityModel<HoglinEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/hoglin/hoglin.png");

	public HoglinEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new HoglinEntityModel<>(context.getPart(EntityModelLayers.HOGLIN)), 0.7F);
	}

	public Identifier getTexture(HoglinEntity hoglinEntity) {
		return TEXTURE;
	}

	protected boolean isShaking(HoglinEntity hoglinEntity) {
		return super.isShaking(hoglinEntity) || hoglinEntity.canConvert();
	}
}
