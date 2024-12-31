package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3804;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3026 implements DebugRenderer.DebugRenderable {
	private final MinecraftClient client;

	public class_3026(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(float tickDelta, long limitTime) {
		PlayerEntity playerEntity = this.client.player;
		IWorld iWorld = this.client.world;
		double d = playerEntity.prevTickX + (playerEntity.x - playerEntity.prevTickX) * (double)tickDelta;
		double e = playerEntity.prevTickY + (playerEntity.y - playerEntity.prevTickY) * (double)tickDelta;
		double f = playerEntity.prevTickZ + (playerEntity.z - playerEntity.prevTickZ) * (double)tickDelta;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.disableTexture();
		BlockPos blockPos = new BlockPos(playerEntity.x, 0.0, playerEntity.z);
		Iterable<BlockPos> iterable = BlockPos.iterate(blockPos.add(-40, 0, -40), blockPos.add(40, 0, 40));
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

		for (BlockPos blockPos2 : iterable) {
			int i = iWorld.method_16372(class_3804.class_3805.WORLD_SURFACE_WG, blockPos2.getX(), blockPos2.getZ());
			if (iWorld.getBlockState(blockPos2.add(0, i, 0).down()).isAir()) {
				WorldRenderer.method_13434(
					bufferBuilder,
					(double)((float)blockPos2.getX() + 0.25F) - d,
					(double)i - e,
					(double)((float)blockPos2.getZ() + 0.25F) - f,
					(double)((float)blockPos2.getX() + 0.75F) - d,
					(double)i + 0.09375 - e,
					(double)((float)blockPos2.getZ() + 0.75F) - f,
					0.0F,
					0.0F,
					1.0F,
					0.5F
				);
			} else {
				WorldRenderer.method_13434(
					bufferBuilder,
					(double)((float)blockPos2.getX() + 0.25F) - d,
					(double)i - e,
					(double)((float)blockPos2.getZ() + 0.25F) - f,
					(double)((float)blockPos2.getX() + 0.75F) - d,
					(double)i + 0.09375 - e,
					(double)((float)blockPos2.getZ() + 0.75F) - f,
					0.0F,
					1.0F,
					0.0F,
					0.5F
				);
			}
		}

		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
