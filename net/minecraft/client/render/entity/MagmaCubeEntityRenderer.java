package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.util.Identifier;

public class MagmaCubeEntityRenderer extends MobEntityRenderer<MagmaCubeEntity> {
	private static final Identifier MAGMA_CUBE_TEX = new Identifier("textures/entity/slime/magmacube.png");

	public MagmaCubeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new MagmaCubeEntityModel(), 0.25F);
	}

	protected Identifier getTexture(MagmaCubeEntity magmaCubeEntity) {
		return MAGMA_CUBE_TEX;
	}

	protected void scale(MagmaCubeEntity magmaCubeEntity, float f) {
		int i = magmaCubeEntity.getSize();
		float g = (magmaCubeEntity.lastStretch + (magmaCubeEntity.stretch - magmaCubeEntity.lastStretch) * f) / ((float)i * 0.5F + 1.0F);
		float h = 1.0F / (g + 1.0F);
		GlStateManager.scale(h * (float)i, 1.0F / h * (float)i, h * (float)i);
	}
}
