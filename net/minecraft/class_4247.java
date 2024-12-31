package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class class_4247 implements DebugRenderer.DebugRenderable {
	private final MinecraftClient field_20883;
	private final Map<BlockPos, BlockPos> field_20884 = Maps.newHashMap();
	private final Map<BlockPos, Float> field_20885 = Maps.newHashMap();
	private final List<BlockPos> field_20886 = Lists.newArrayList();

	public class_4247(MinecraftClient minecraftClient) {
		this.field_20883 = minecraftClient;
	}

	public void method_19354(BlockPos blockPos, List<BlockPos> list, List<Float> list2) {
		for (int i = 0; i < list.size(); i++) {
			this.field_20884.put(list.get(i), blockPos);
			this.field_20885.put(list.get(i), list2.get(i));
		}

		this.field_20886.add(blockPos);
	}

	@Override
	public void render(float tickDelta, long limitTime) {
		PlayerEntity playerEntity = this.field_20883.player;
		BlockView blockView = this.field_20883.world;
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
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

		for (Entry<BlockPos, BlockPos> entry : this.field_20884.entrySet()) {
			BlockPos blockPos2 = (BlockPos)entry.getKey();
			BlockPos blockPos3 = (BlockPos)entry.getValue();
			float g = (float)(blockPos3.getX() * 128 % 256) / 256.0F;
			float h = (float)(blockPos3.getY() * 128 % 256) / 256.0F;
			float i = (float)(blockPos3.getZ() * 128 % 256) / 256.0F;
			float j = (Float)this.field_20885.get(blockPos2);
			if (blockPos.method_19965(blockPos2) < 160.0) {
				WorldRenderer.method_13434(
					bufferBuilder,
					(double)((float)blockPos2.getX() + 0.5F) - d - (double)j,
					(double)((float)blockPos2.getY() + 0.5F) - e - (double)j,
					(double)((float)blockPos2.getZ() + 0.5F) - f - (double)j,
					(double)((float)blockPos2.getX() + 0.5F) - d + (double)j,
					(double)((float)blockPos2.getY() + 0.5F) - e + (double)j,
					(double)((float)blockPos2.getZ() + 0.5F) - f + (double)j,
					g,
					h,
					i,
					0.5F
				);
			}
		}

		for (BlockPos blockPos4 : this.field_20886) {
			if (blockPos.method_19965(blockPos4) < 160.0) {
				WorldRenderer.method_13434(
					bufferBuilder,
					(double)blockPos4.getX() - d,
					(double)blockPos4.getY() - e,
					(double)blockPos4.getZ() - f,
					(double)((float)blockPos4.getX() + 1.0F) - d,
					(double)((float)blockPos4.getY() + 1.0F) - e,
					(double)((float)blockPos4.getZ() + 1.0F) - f,
					1.0F,
					1.0F,
					1.0F,
					1.0F
				);
			}
		}

		tessellator.draw();
		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
