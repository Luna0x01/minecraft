package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class BipedEntityRenderer<T extends MobEntity> extends MobEntityRenderer<T> {
	private static final Identifier STEVE_TEXTURE = new Identifier("textures/entity/steve.png");

	public BipedEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, BiPedModel biPedModel, float f) {
		super(entityRenderDispatcher, biPedModel, f);
		this.addFeature(new HeadFeatureRenderer(biPedModel.head));
		this.addFeature(new ElytraFeatureRenderer(this));
		this.addFeature(new HeldItemRenderer(this));
	}

	protected Identifier getTexture(T mobEntity) {
		return STEVE_TEXTURE;
	}
}
