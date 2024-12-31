package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.Identifier;

public class WitchEntityRenderer extends MobEntityRenderer<WitchEntity> {
	private static final Identifier WITCH_TEX = new Identifier("textures/entity/witch.png");

	public WitchEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new WitchEntityModel(0.0F), 0.5F);
		this.addFeature(new WitchHeldItemFeatureRenderer(this));
	}

	public void render(WitchEntity witchEntity, double d, double e, double f, float g, float h) {
		((WitchEntityModel)this.model).field_5133 = witchEntity.getMainHandStack() != null;
		super.render(witchEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(WitchEntity witchEntity) {
		return WITCH_TEX;
	}

	@Override
	public void translate() {
		GlStateManager.translate(0.0F, 0.1875F, 0.0F);
	}

	protected void scale(WitchEntity witchEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scale(g, g, g);
	}
}
