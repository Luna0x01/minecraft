package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.shapes.VoxelShape;

public class class_3097 implements DebugRenderer.DebugRenderable {
	private final MinecraftClient client;
	private double field_20887 = Double.MIN_VALUE;
	private List<VoxelShape> field_20888 = Collections.emptyList();

	public class_3097(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(float tickDelta, long limitTime) {
		PlayerEntity playerEntity = this.client.player;
		double d = (double)Util.method_20230();
		if (d - this.field_20887 > 1.0E8) {
			this.field_20887 = d;
			this.field_20888 = (List<VoxelShape>)playerEntity.world.method_16384(playerEntity, playerEntity.getBoundingBox().expand(6.0)).collect(Collectors.toList());
		}

		double e = playerEntity.prevTickX + (playerEntity.x - playerEntity.prevTickX) * (double)tickDelta;
		double f = playerEntity.prevTickY + (playerEntity.y - playerEntity.prevTickY) * (double)tickDelta;
		double g = playerEntity.prevTickZ + (playerEntity.z - playerEntity.prevTickZ) * (double)tickDelta;
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.method_12304(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);

		for (VoxelShape voxelShape : this.field_20888) {
			WorldRenderer.method_19159(voxelShape, -e, -f, -g, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
