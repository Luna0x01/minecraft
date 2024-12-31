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
		float h = 4.5F;
		float i = 4.5F;
		GlStateManager.scale(4.5F, 4.5F, 4.5F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
