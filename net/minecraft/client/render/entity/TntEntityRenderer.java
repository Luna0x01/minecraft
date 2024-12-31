package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class TntEntityRenderer extends EntityRenderer<TntEntity> {
	public TntEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.shadowSize = 0.5F;
	}

	public void render(TntEntity tntEntity, double d, double e, double f, float g, float h) {
		BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d, (float)e + 0.5F, (float)f);
		if ((float)tntEntity.fuseTimer - h + 1.0F < 10.0F) {
			float i = 1.0F - ((float)tntEntity.fuseTimer - h + 1.0F) / 10.0F;
			i = MathHelper.clamp(i, 0.0F, 1.0F);
			i *= i;
			i *= i;
			float j = 1.0F + i * 0.3F;
			GlStateManager.scale(j, j, j);
		}

		float k = (1.0F - ((float)tntEntity.fuseTimer - h + 1.0F) / 100.0F) * 0.8F;
		this.bindTexture(tntEntity);
		GlStateManager.translate(-0.5F, -0.5F, 0.5F);
		blockRenderManager.renderBlockEntity(Blocks.TNT.getDefaultState(), tntEntity.getBrightnessAtEyes(h));
		GlStateManager.translate(0.0F, 0.0F, 1.0F);
		if (tntEntity.fuseTimer / 5 % 2 == 0) {
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 772);
			GlStateManager.color(1.0F, 1.0F, 1.0F, k);
			GlStateManager.polygonOffset(-3.0F, -3.0F);
			GlStateManager.enablePolyOffset();
			blockRenderManager.renderBlockEntity(Blocks.TNT.getDefaultState(), 1.0F);
			GlStateManager.polygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolyOffset();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
		}

		GlStateManager.popMatrix();
		super.render(tntEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(TntEntity tntEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
