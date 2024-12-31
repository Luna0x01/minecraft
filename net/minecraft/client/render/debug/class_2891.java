package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class class_2891 implements DebugRenderer.DebugRenderable {
	private final MinecraftClient client;
	private final Map<Integer, PathMinHeap> field_13623 = Maps.newHashMap();
	private final Map<Integer, Float> field_13624 = Maps.newHashMap();
	private final Map<Integer, Long> field_13625 = Maps.newHashMap();
	private PlayerEntity player;
	private double field_14978;
	private double field_14979;
	private double field_14980;

	public class_2891(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void method_12434(int i, PathMinHeap pathMinHeap, float f) {
		this.field_13623.put(i, pathMinHeap);
		this.field_13625.put(i, Util.method_20227());
		this.field_13624.put(i, f);
	}

	@Override
	public void render(float tickDelta, long limitTime) {
		if (!this.field_13623.isEmpty()) {
			long l = Util.method_20227();
			this.player = this.client.player;
			this.field_14978 = this.player.prevTickX + (this.player.x - this.player.prevTickX) * (double)tickDelta;
			this.field_14979 = this.player.prevTickY + (this.player.y - this.player.prevTickY) * (double)tickDelta;
			this.field_14980 = this.player.prevTickZ + (this.player.z - this.player.prevTickZ) * (double)tickDelta;
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			GlStateManager.color(0.0F, 1.0F, 0.0F, 0.75F);
			GlStateManager.disableTexture();
			GlStateManager.method_12304(6.0F);

			for (Integer integer : this.field_13623.keySet()) {
				PathMinHeap pathMinHeap = (PathMinHeap)this.field_13623.get(integer);
				float f = (Float)this.field_13624.get(integer);
				this.method_13453(tickDelta, pathMinHeap);
				PathNode pathNode = pathMinHeap.method_13399();
				if (!(this.method_13454(pathNode) > 40.0F)) {
					WorldRenderer.method_13433(
						new Box(
								(double)((float)pathNode.posX + 0.25F),
								(double)((float)pathNode.posY + 0.25F),
								(double)pathNode.posZ + 0.25,
								(double)((float)pathNode.posX + 0.75F),
								(double)((float)pathNode.posY + 0.75F),
								(double)((float)pathNode.posZ + 0.75F)
							)
							.offset(-this.field_14978, -this.field_14979, -this.field_14980),
						0.0F,
						1.0F,
						0.0F,
						0.5F
					);

					for (int i = 0; i < pathMinHeap.method_11936(); i++) {
						PathNode pathNode2 = pathMinHeap.method_11925(i);
						if (!(this.method_13454(pathNode2) > 40.0F)) {
							float g = i == pathMinHeap.method_11937() ? 1.0F : 0.0F;
							float h = i == pathMinHeap.method_11937() ? 0.0F : 1.0F;
							WorldRenderer.method_13433(
								new Box(
										(double)((float)pathNode2.posX + 0.5F - f),
										(double)((float)pathNode2.posY + 0.01F * (float)i),
										(double)((float)pathNode2.posZ + 0.5F - f),
										(double)((float)pathNode2.posX + 0.5F + f),
										(double)((float)pathNode2.posY + 0.25F + 0.01F * (float)i),
										(double)((float)pathNode2.posZ + 0.5F + f)
									)
									.offset(-this.field_14978, -this.field_14979, -this.field_14980),
								g,
								0.0F,
								h,
								0.5F
							);
						}
					}
				}
			}

			for (Integer integer2 : this.field_13623.keySet()) {
				PathMinHeap pathMinHeap2 = (PathMinHeap)this.field_13623.get(integer2);

				for (PathNode pathNode3 : pathMinHeap2.method_13398()) {
					if (!(this.method_13454(pathNode3) > 40.0F)) {
						DebugRenderer.method_13450(
							String.format("%s", pathNode3.field_13074), (double)pathNode3.posX + 0.5, (double)pathNode3.posY + 0.75, (double)pathNode3.posZ + 0.5, tickDelta, -65536
						);
						DebugRenderer.method_13450(
							String.format(Locale.ROOT, "%.2f", pathNode3.field_13073),
							(double)pathNode3.posX + 0.5,
							(double)pathNode3.posY + 0.25,
							(double)pathNode3.posZ + 0.5,
							tickDelta,
							-65536
						);
					}
				}

				for (PathNode pathNode4 : pathMinHeap2.method_13397()) {
					if (!(this.method_13454(pathNode4) > 40.0F)) {
						DebugRenderer.method_13450(
							String.format("%s", pathNode4.field_13074),
							(double)pathNode4.posX + 0.5,
							(double)pathNode4.posY + 0.75,
							(double)pathNode4.posZ + 0.5,
							tickDelta,
							-16776961
						);
						DebugRenderer.method_13450(
							String.format(Locale.ROOT, "%.2f", pathNode4.field_13073),
							(double)pathNode4.posX + 0.5,
							(double)pathNode4.posY + 0.25,
							(double)pathNode4.posZ + 0.5,
							tickDelta,
							-16776961
						);
					}
				}

				for (int j = 0; j < pathMinHeap2.method_11936(); j++) {
					PathNode pathNode5 = pathMinHeap2.method_11925(j);
					if (!(this.method_13454(pathNode5) > 40.0F)) {
						DebugRenderer.method_13450(
							String.format("%s", pathNode5.field_13074), (double)pathNode5.posX + 0.5, (double)pathNode5.posY + 0.75, (double)pathNode5.posZ + 0.5, tickDelta, -1
						);
						DebugRenderer.method_13450(
							String.format(Locale.ROOT, "%.2f", pathNode5.field_13073),
							(double)pathNode5.posX + 0.5,
							(double)pathNode5.posY + 0.25,
							(double)pathNode5.posZ + 0.5,
							tickDelta,
							-1
						);
					}
				}
			}

			for (Integer integer3 : (Integer[])this.field_13625.keySet().toArray(new Integer[0])) {
				if (l - (Long)this.field_13625.get(integer3) > 20000L) {
					this.field_13623.remove(integer3);
					this.field_13625.remove(integer3);
				}
			}

			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	public void method_13453(float f, PathMinHeap pathMinHeap) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);

		for (int i = 0; i < pathMinHeap.method_11936(); i++) {
			PathNode pathNode = pathMinHeap.method_11925(i);
			if (!(this.method_13454(pathNode) > 40.0F)) {
				float g = (float)i / (float)pathMinHeap.method_11936() * 0.33F;
				int j = i == 0 ? 0 : MathHelper.hsvToRgb(g, 0.9F, 0.9F);
				int k = j >> 16 & 0xFF;
				int l = j >> 8 & 0xFF;
				int m = j & 0xFF;
				bufferBuilder.vertex(
						(double)pathNode.posX - this.field_14978 + 0.5, (double)pathNode.posY - this.field_14979 + 0.5, (double)pathNode.posZ - this.field_14980 + 0.5
					)
					.color(k, l, m, 255)
					.next();
			}
		}

		tessellator.draw();
	}

	private float method_13454(PathNode pathNode) {
		return (float)(
			Math.abs((double)pathNode.posX - this.player.x) + Math.abs((double)pathNode.posY - this.player.y) + Math.abs((double)pathNode.posZ - this.player.z)
		);
	}
}
