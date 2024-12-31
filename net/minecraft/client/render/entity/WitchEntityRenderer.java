package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_4201;
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.Identifier;

public class WitchEntityRenderer extends MobEntityRenderer<WitchEntity> {
	private static final Identifier WITCH_TEX = new Identifier("textures/entity/witch.png");

	public WitchEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4201(0.0F), 0.5F);
		this.addFeature(new WitchHeldItemFeatureRenderer(this));
	}

	public class_4201 getModel() {
		return (class_4201)super.getModel();
	}

	public void render(WitchEntity witchEntity, double d, double e, double f, float g, float h) {
		((class_4201)this.model).method_18945(!witchEntity.getMainHandStack().isEmpty());
		super.render(witchEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(WitchEntity witchEntity) {
		return WITCH_TEX;
	}

	protected void scale(WitchEntity witchEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
	}
}
