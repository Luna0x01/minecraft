package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.SlimeFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;

public class SlimeEntityRenderer extends MobEntityRenderer<SlimeEntity> {
	private static final Identifier SLIME_TEX = new Identifier("textures/entity/slime/slime.png");

	public SlimeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
		this.addFeature(new SlimeFeatureRenderer(this));
	}

	public void render(SlimeEntity slimeEntity, double d, double e, double f, float g, float h) {
		this.shadowSize = 0.25F * (float)slimeEntity.getSize();
		super.render(slimeEntity, d, e, f, g, h);
	}

	protected void scale(SlimeEntity slimeEntity, float f) {
		float g = 0.999F;
		GlStateManager.scale(0.999F, 0.999F, 0.999F);
		float h = (float)slimeEntity.getSize();
		float i = (slimeEntity.lastStretch + (slimeEntity.stretch - slimeEntity.lastStretch) * f) / (h * 0.5F + 1.0F);
		float j = 1.0F / (i + 1.0F);
		GlStateManager.scale(j * h, 1.0F / j * h, j * h);
	}

	protected Identifier getTexture(SlimeEntity slimeEntity) {
		return SLIME_TEX;
	}
}
