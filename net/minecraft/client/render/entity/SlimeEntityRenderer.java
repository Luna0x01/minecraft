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
		float g = (float)slimeEntity.getSize();
		float h = (slimeEntity.lastStretch + (slimeEntity.stretch - slimeEntity.lastStretch) * f) / (g * 0.5F + 1.0F);
		float i = 1.0F / (h + 1.0F);
		GlStateManager.scale(i * g, 1.0F / i * g, i * g);
	}

	protected Identifier getTexture(SlimeEntity slimeEntity) {
		return SLIME_TEX;
	}
}
