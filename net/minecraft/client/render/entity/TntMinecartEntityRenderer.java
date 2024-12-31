package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

public class TntMinecartEntityRenderer extends MinecartEntityRenderer<TntMinecartEntity> {
	public TntMinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected void method_5180(TntMinecartEntity tntMinecartEntity, float f, BlockState blockState) {
		int i = tntMinecartEntity.getFuseTicks();
		if (i > -1 && (float)i - f + 1.0F < 10.0F) {
			float g = 1.0F - ((float)i - f + 1.0F) / 10.0F;
			g = MathHelper.clamp(g, 0.0F, 1.0F);
			g *= g;
			g *= g;
			float h = 1.0F + g * 0.3F;
			GlStateManager.scale(h, h, h);
		}

		super.method_5180(tntMinecartEntity, f, blockState);
		if (i > -1 && i / 5 % 2 == 0) {
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.DST_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, (1.0F - ((float)i - f + 1.0F) / 100.0F) * 0.8F);
			GlStateManager.pushMatrix();
			blockRenderManager.renderBlockEntity(Blocks.TNT.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
		}
	}
}
