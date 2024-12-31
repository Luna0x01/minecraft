package net.minecraft;

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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_4249 implements DebugRenderer.DebugRenderable {
	private final MinecraftClient field_20899;
	private final Map<Integer, Map<String, BlockBox>> field_20900 = Maps.newHashMap();
	private final Map<Integer, Map<String, BlockBox>> field_20901 = Maps.newHashMap();
	private final Map<Integer, Map<String, Boolean>> field_20902 = Maps.newHashMap();

	public class_4249(MinecraftClient minecraftClient) {
		this.field_20899 = minecraftClient;
	}

	@Override
	public void render(float tickDelta, long limitTime) {
		PlayerEntity playerEntity = this.field_20899.player;
		IWorld iWorld = this.field_20899.world;
		int i = iWorld.method_3588().method_17966();
		double d = playerEntity.prevTickX + (playerEntity.x - playerEntity.prevTickX) * (double)tickDelta;
		double e = playerEntity.prevTickY + (playerEntity.y - playerEntity.prevTickY) * (double)tickDelta;
		double f = playerEntity.prevTickZ + (playerEntity.z - playerEntity.prevTickZ) * (double)tickDelta;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.disableTexture();
		GlStateManager.disableDepthTest();
		BlockPos blockPos = new BlockPos(playerEntity.x, 0.0, playerEntity.z);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
		GlStateManager.method_12304(1.0F);
		if (this.field_20900.containsKey(i)) {
			for (BlockBox blockBox : ((Map)this.field_20900.get(i)).values()) {
				if (blockPos.distanceTo(blockBox.minX, blockBox.minY, blockBox.minZ) < 500.0) {
					WorldRenderer.method_13431(
						bufferBuilder,
						(double)blockBox.minX - d,
						(double)blockBox.minY - e,
						(double)blockBox.minZ - f,
						(double)(blockBox.maxX + 1) - d,
						(double)(blockBox.maxY + 1) - e,
						(double)(blockBox.maxZ + 1) - f,
						1.0F,
						1.0F,
						1.0F,
						1.0F
					);
				}
			}
		}

		if (this.field_20901.containsKey(i)) {
			for (Entry<String, BlockBox> entry : ((Map)this.field_20901.get(i)).entrySet()) {
				String string = (String)entry.getKey();
				BlockBox blockBox2 = (BlockBox)entry.getValue();
				Boolean boolean_ = (Boolean)((Map)this.field_20902.get(i)).get(string);
				if (blockPos.distanceTo(blockBox2.minX, blockBox2.minY, blockBox2.minZ) < 500.0) {
					if (boolean_) {
						WorldRenderer.method_13431(
							bufferBuilder,
							(double)blockBox2.minX - d,
							(double)blockBox2.minY - e,
							(double)blockBox2.minZ - f,
							(double)(blockBox2.maxX + 1) - d,
							(double)(blockBox2.maxY + 1) - e,
							(double)(blockBox2.maxZ + 1) - f,
							0.0F,
							1.0F,
							0.0F,
							1.0F
						);
					} else {
						WorldRenderer.method_13431(
							bufferBuilder,
							(double)blockBox2.minX - d,
							(double)blockBox2.minY - e,
							(double)blockBox2.minZ - f,
							(double)(blockBox2.maxX + 1) - d,
							(double)(blockBox2.maxY + 1) - e,
							(double)(blockBox2.maxZ + 1) - f,
							0.0F,
							0.0F,
							1.0F,
							1.0F
						);
					}
				}
			}
		}

		tessellator.draw();
		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}

	public void method_19355(BlockBox blockBox, List<BlockBox> list, List<Boolean> list2, int i) {
		if (!this.field_20900.containsKey(i)) {
			this.field_20900.put(i, Maps.newHashMap());
		}

		if (!this.field_20901.containsKey(i)) {
			this.field_20901.put(i, Maps.newHashMap());
			this.field_20902.put(i, Maps.newHashMap());
		}

		((Map)this.field_20900.get(i)).put(blockBox.toString(), blockBox);

		for (int j = 0; j < list.size(); j++) {
			BlockBox blockBox2 = (BlockBox)list.get(j);
			Boolean boolean_ = (Boolean)list2.get(j);
			((Map)this.field_20901.get(i)).put(blockBox2.toString(), blockBox2);
			((Map)this.field_20902.get(i)).put(blockBox2.toString(), boolean_);
		}
	}
}
