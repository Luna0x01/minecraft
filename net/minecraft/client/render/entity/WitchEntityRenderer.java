package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.Identifier;

public class WitchEntityRenderer extends MobEntityRenderer<WitchEntity, WitchEntityModel<WitchEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/witch.png");

	public WitchEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new WitchEntityModel<>(0.0F), 0.5F);
		this.addFeature(new WitchHeldItemFeatureRenderer<>(this));
	}

	public void method_4155(WitchEntity witchEntity, double d, double e, double f, float g, float h) {
		this.model.method_2840(!witchEntity.getMainHandStack().isEmpty());
		super.method_4072(witchEntity, d, e, f, g, h);
	}

	protected Identifier method_4154(WitchEntity witchEntity) {
		return SKIN;
	}

	protected void method_4157(WitchEntity witchEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
	}
}
