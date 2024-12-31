package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.passive.IronGolemEntity;

public class IronGolemFlowerFeatureRenderer implements FeatureRenderer<IronGolemEntity> {
	private final IronGolemEntityRenderer ironGolemRenderer;

	public IronGolemFlowerFeatureRenderer(IronGolemEntityRenderer ironGolemEntityRenderer) {
		this.ironGolemRenderer = ironGolemEntityRenderer;
	}

	public void render(IronGolemEntity ironGolemEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (ironGolemEntity.getLookingAtVillagerTicks() != 0) {
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.rotate(5.0F + 180.0F * ((IronGolemEntityModel)this.ironGolemRenderer.getModel()).field_1553.posX / (float) Math.PI, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(-0.9375F, -0.625F, -0.9375F);
			float m = 0.5F;
			GlStateManager.scale(m, -m, m);
			int n = ironGolemEntity.getLightmapCoordinates(h);
			int o = n % 65536;
			int p = n / 65536;
			GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)o / 1.0F, (float)p / 1.0F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.ironGolemRenderer.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			blockRenderManager.renderBlockEntity(Blocks.RED_FLOWER.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
