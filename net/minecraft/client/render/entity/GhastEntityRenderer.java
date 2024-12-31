package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.Identifier;

public class GhastEntityRenderer extends MobEntityRenderer<GhastEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/ghast/ghast.png");
	private static final Identifier ANGRY_TEXTURE = new Identifier("textures/entity/ghast/ghast_shooting.png");

	public GhastEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new GhastEntityModel(), 0.5F);
	}

	protected Identifier getTexture(GhastEntity ghastEntity) {
		return ghastEntity.isShooting() ? ANGRY_TEXTURE : TEXTURE;
	}

	protected void scale(GhastEntity ghastEntity, float f) {
		float g = 1.0F;
		float h = (8.0F + g) / 2.0F;
		float i = (8.0F + 1.0F / g) / 2.0F;
		GlStateManager.scale(i, h, i);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
