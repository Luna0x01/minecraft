package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.RenderBlockView;

public class class_4248 implements DebugRenderer.DebugRenderable {
	private final MinecraftClient field_20898;

	public class_4248(MinecraftClient minecraftClient) {
		this.field_20898 = minecraftClient;
	}

	@Override
	public void render(float tickDelta, long limitTime) {
		PlayerEntity playerEntity = this.field_20898.player;
		RenderBlockView renderBlockView = this.field_20898.world;
		double d = playerEntity.prevTickX + (playerEntity.x - playerEntity.prevTickX) * (double)tickDelta;
		double e = playerEntity.prevTickY + (playerEntity.y - playerEntity.prevTickY) * (double)tickDelta;
		double f = playerEntity.prevTickZ + (playerEntity.z - playerEntity.prevTickZ) * (double)tickDelta;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.disableTexture();
		BlockPos blockPos = new BlockPos(playerEntity.x, playerEntity.y, playerEntity.z);

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-5, -5, -5), blockPos.add(5, 5, 5))) {
			int i = renderBlockView.method_16370(LightType.SKY, blockPos2);
			float g = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
			int j = MathHelper.hsvToRgb(g, 0.9F, 0.9F);
			if (i != 15) {
				DebugRenderer.method_13450(String.valueOf(i), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, 1.0F, j);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
