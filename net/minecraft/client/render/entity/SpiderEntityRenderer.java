package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.SpiderEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

public class SpiderEntityRenderer<T extends SpiderEntity> extends MobEntityRenderer<T> {
	private static final Identifier SPIDER_TEX = new Identifier("textures/entity/spider/spider.png");

	public SpiderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SpiderEntityModel(), 1.0F);
		this.addFeature(new SpiderEyesFeatureRenderer<>(this));
	}

	protected float method_5771(T spiderEntity) {
		return 180.0F;
	}

	protected Identifier getTexture(T spiderEntity) {
		return SPIDER_TEX;
	}
}
