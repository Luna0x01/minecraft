package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class BipedEntityRenderer<T extends MobEntity> extends MobEntityRenderer<T> {
	private static final Identifier STEVE_TEXTURE = new Identifier("textures/entity/steve.png");
	protected BiPedModel bipedModel;
	protected float field_2122;

	public BipedEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, BiPedModel biPedModel, float f) {
		this(entityRenderDispatcher, biPedModel, f, 1.0F);
		this.addFeature(new HeldItemRenderer(this));
	}

	public BipedEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, BiPedModel biPedModel, float f, float g) {
		super(entityRenderDispatcher, biPedModel, f);
		this.bipedModel = biPedModel;
		this.field_2122 = g;
		this.addFeature(new HeadFeatureRenderer(biPedModel.head));
	}

	protected Identifier getTexture(T mobEntity) {
		return STEVE_TEXTURE;
	}

	@Override
	public void translate() {
		GlStateManager.translate(0.0F, 0.1875F, 0.0F);
	}
}
