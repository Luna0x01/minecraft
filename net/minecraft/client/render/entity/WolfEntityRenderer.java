package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

public class WolfEntityRenderer extends MobEntityRenderer<WolfEntity> {
	private static final Identifier WOLF_TEX = new Identifier("textures/entity/wolf/wolf.png");
	private static final Identifier WOLF_TAME = new Identifier("textures/entity/wolf/wolf_tame.png");
	private static final Identifier WOLF_ANGRY = new Identifier("textures/entity/wolf/wolf_angry.png");

	public WolfEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
		this.addFeature(new WolfCollarFeatureRenderer(this));
	}

	protected float method_5783(WolfEntity wolfEntity, float f) {
		return wolfEntity.method_2882();
	}

	public void render(WolfEntity wolfEntity, double d, double e, double f, float g, float h) {
		if (wolfEntity.method_2881()) {
			float i = wolfEntity.getBrightnessAtEyes(h) * wolfEntity.method_2879(h);
			GlStateManager.color(i, i, i);
		}

		super.render(wolfEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(WolfEntity wolfEntity) {
		if (wolfEntity.isTamed()) {
			return WOLF_TAME;
		} else {
			return wolfEntity.isAngry() ? WOLF_ANGRY : WOLF_TEX;
		}
	}
}
