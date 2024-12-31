package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.entity.PolarBearEntity;
import net.minecraft.util.Identifier;

public class PolarBearEntityRenderer extends MobEntityRenderer<PolarBearEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/bear/polarbear.png");

	public PolarBearEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PolarBearEntityModel(), 0.7F);
	}

	protected Identifier getTexture(PolarBearEntity polarBearEntity) {
		return TEXTURE;
	}

	public void render(PolarBearEntity polarBearEntity, double d, double e, double f, float g, float h) {
		super.render(polarBearEntity, d, e, f, g, h);
	}

	protected void scale(PolarBearEntity polarBearEntity, float f) {
		GlStateManager.scale(1.2F, 1.2F, 1.2F);
		super.scale(polarBearEntity, f);
	}
}
