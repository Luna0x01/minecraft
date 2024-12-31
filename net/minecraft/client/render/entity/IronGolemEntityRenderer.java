package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

public class IronGolemEntityRenderer extends MobEntityRenderer<IronGolemEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/iron_golem.png");

	public IronGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new IronGolemEntityModel(), 0.5F);
		this.addFeature(new IronGolemFlowerFeatureRenderer(this));
	}

	protected Identifier getTexture(IronGolemEntity ironGolemEntity) {
		return TEXTURE;
	}

	protected void method_5777(IronGolemEntity ironGolemEntity, float f, float g, float h) {
		super.method_5777(ironGolemEntity, f, g, h);
		if (!((double)ironGolemEntity.field_6749 < 0.01)) {
			float i = 13.0F;
			float j = ironGolemEntity.field_6750 - ironGolemEntity.field_6749 * (1.0F - h) + 6.0F;
			float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
			GlStateManager.rotate(6.5F * k, 0.0F, 0.0F, 1.0F);
		}
	}
}
