package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class BipedEntityRenderer<T extends MobEntity, M extends BipedEntityModel<T>> extends MobEntityRenderer<T, M> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/steve.png");

	public BipedEntityRenderer(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
		this(ctx, model, shadowRadius, 1.0F, 1.0F, 1.0F);
	}

	public BipedEntityRenderer(EntityRendererFactory.Context ctx, M model, float shadowRadius, float scaleX, float scaleY, float scaleZ) {
		super(ctx, model, shadowRadius);
		this.addFeature(new HeadFeatureRenderer<>(this, ctx.getModelLoader(), scaleX, scaleY, scaleZ));
		this.addFeature(new ElytraFeatureRenderer<>(this, ctx.getModelLoader()));
		this.addFeature(new HeldItemFeatureRenderer<>(this));
	}

	public Identifier getTexture(T mobEntity) {
		return TEXTURE;
	}
}
