package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.MooshroomEntityRenderer;
import net.minecraft.client.render.entity.model.QuadruPedEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.passive.MooshroomEntity;

public class MooshroomMushroomFeatureRenderer implements FeatureRenderer<MooshroomEntity> {
	private final MooshroomEntityRenderer mooshroomRenderer;

	public MooshroomMushroomFeatureRenderer(MooshroomEntityRenderer mooshroomEntityRenderer) {
		this.mooshroomRenderer = mooshroomEntityRenderer;
	}

	public void render(MooshroomEntity mooshroomEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!mooshroomEntity.isBaby() && !mooshroomEntity.isInvisible()) {
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			this.mooshroomRenderer.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			GlStateManager.enableCull();
			GlStateManager.method_12284(GlStateManager.class_2865.FRONT);
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F, -1.0F, 1.0F);
			GlStateManager.translate(0.2F, 0.35F, 0.5F);
			GlStateManager.rotate(42.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translate(-0.5F, -0.5F, 0.5F);
			blockRenderManager.renderBlockEntity(Blocks.RED_MUSHROOM.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.1F, 0.0F, -0.6F);
			GlStateManager.rotate(42.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-0.5F, -0.5F, 0.5F);
			blockRenderManager.renderBlockEntity(Blocks.RED_MUSHROOM.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			((QuadruPedEntityModel)this.mooshroomRenderer.getModel()).head.preRender(0.0625F);
			GlStateManager.scale(1.0F, -1.0F, 1.0F);
			GlStateManager.translate(0.0F, 0.7F, -0.2F);
			GlStateManager.rotate(12.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-0.5F, -0.5F, 0.5F);
			blockRenderManager.renderBlockEntity(Blocks.RED_MUSHROOM.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.method_12284(GlStateManager.class_2865.BACK);
			GlStateManager.disableCull();
		}
	}

	@Override
	public boolean combineTextures() {
		return true;
	}
}
