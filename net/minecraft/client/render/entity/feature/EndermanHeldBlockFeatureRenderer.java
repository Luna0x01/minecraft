package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.mob.EndermanEntity;

public class EndermanHeldBlockFeatureRenderer implements FeatureRenderer<EndermanEntity> {
	private final EndermanEntityRenderer endermanRenderer;

	public EndermanHeldBlockFeatureRenderer(EndermanEntityRenderer endermanEntityRenderer) {
		this.endermanRenderer = endermanEntityRenderer;
	}

	public void render(EndermanEntity endermanEntity, float f, float g, float h, float i, float j, float k, float l) {
		BlockState blockState = endermanEntity.getCarriedBlock();
		if (blockState != null) {
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.6875F, -0.75F);
			GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.25F, 0.1875F, 0.25F);
			float m = 0.5F;
			GlStateManager.scale(-0.5F, -0.5F, 0.5F);
			int n = endermanEntity.getLightmapCoordinates();
			int o = n % 65536;
			int p = n / 65536;
			GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)o, (float)p);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.endermanRenderer.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			blockRenderManager.renderBlockEntity(blockState, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
