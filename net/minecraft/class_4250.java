package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class class_4250 implements DebugRenderer.DebugRenderable {
	private final MinecraftClient field_20903;
	private final List<BlockPos> field_20904 = Lists.newArrayList();
	private final List<Float> field_20905 = Lists.newArrayList();
	private final List<Float> field_20906 = Lists.newArrayList();
	private final List<Float> field_20907 = Lists.newArrayList();
	private final List<Float> field_20908 = Lists.newArrayList();
	private final List<Float> field_20909 = Lists.newArrayList();

	public class_4250(MinecraftClient minecraftClient) {
		this.field_20903 = minecraftClient;
	}

	public void method_19356(BlockPos blockPos, float f, float g, float h, float i, float j) {
		this.field_20904.add(blockPos);
		this.field_20905.add(f);
		this.field_20906.add(j);
		this.field_20907.add(g);
		this.field_20908.add(h);
		this.field_20909.add(i);
	}

	@Override
	public void render(float tickDelta, long limitTime) {
		PlayerEntity playerEntity = this.field_20903.player;
		BlockView blockView = this.field_20903.world;
		double d = playerEntity.prevTickX + (playerEntity.x - playerEntity.prevTickX) * (double)tickDelta;
		double e = playerEntity.prevTickY + (playerEntity.y - playerEntity.prevTickY) * (double)tickDelta;
		double f = playerEntity.prevTickZ + (playerEntity.z - playerEntity.prevTickZ) * (double)tickDelta;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.disableTexture();
		new BlockPos(playerEntity.x, 0.0, playerEntity.z);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

		for (int i = 0; i < this.field_20904.size(); i++) {
			BlockPos blockPos2 = (BlockPos)this.field_20904.get(i);
			Float float_ = (Float)this.field_20905.get(i);
			float g = float_ / 2.0F;
			WorldRenderer.method_13434(
				bufferBuilder,
				(double)((float)blockPos2.getX() + 0.5F - g) - d,
				(double)((float)blockPos2.getY() + 0.5F - g) - e,
				(double)((float)blockPos2.getZ() + 0.5F - g) - f,
				(double)((float)blockPos2.getX() + 0.5F + g) - d,
				(double)((float)blockPos2.getY() + 0.5F + g) - e,
				(double)((float)blockPos2.getZ() + 0.5F + g) - f,
				(Float)this.field_20907.get(i),
				(Float)this.field_20908.get(i),
				(Float)this.field_20909.get(i),
				(Float)this.field_20906.get(i)
			);
		}

		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
