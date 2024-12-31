package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.util.Identifier;

public class CaveSpiderEntityRenderer extends SpiderEntityRenderer<CaveSpiderEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/spider/cave_spider.png");

	public CaveSpiderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.shadowSize *= 0.7F;
	}

	protected void scale(CaveSpiderEntity caveSpiderEntity, float f) {
		GlStateManager.scale(0.7F, 0.7F, 0.7F);
	}

	protected Identifier getTexture(CaveSpiderEntity caveSpiderEntity) {
		return TEXTURE;
	}
}
