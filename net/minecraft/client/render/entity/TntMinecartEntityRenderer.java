package net.minecraft.client.render.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

public class TntMinecartEntityRenderer extends MinecartEntityRenderer<TntMinecartEntity> {
	public TntMinecartEntityRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.TNT_MINECART);
	}

	protected void renderBlock(
		TntMinecartEntity tntMinecartEntity, float f, BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		int j = tntMinecartEntity.getFuseTicks();
		if (j > -1 && (float)j - f + 1.0F < 10.0F) {
			float g = 1.0F - ((float)j - f + 1.0F) / 10.0F;
			g = MathHelper.clamp(g, 0.0F, 1.0F);
			g *= g;
			g *= g;
			float h = 1.0F + g * 0.3F;
			matrixStack.scale(h, h, h);
		}

		renderFlashingBlock(blockState, matrixStack, vertexConsumerProvider, i, j > -1 && j / 5 % 2 == 0);
	}

	public static void renderFlashingBlock(BlockState blockState, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean drawFlash) {
		int i;
		if (drawFlash) {
			i = OverlayTexture.packUv(OverlayTexture.getU(1.0F), 10);
		} else {
			i = OverlayTexture.DEFAULT_UV;
		}

		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vertexConsumers, light, i);
	}
}
