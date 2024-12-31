package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.Identifier;

public class PolarBearEntityRenderer extends MobEntityRenderer<PolarBearEntity, PolarBearEntityModel<PolarBearEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/bear/polarbear.png");

	public PolarBearEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PolarBearEntityModel<>(), 0.9F);
	}

	protected Identifier method_4097(PolarBearEntity polarBearEntity) {
		return SKIN;
	}

	protected void method_4099(PolarBearEntity polarBearEntity, float f) {
		GlStateManager.scalef(1.2F, 1.2F, 1.2F);
		super.scale(polarBearEntity, f);
	}
}
