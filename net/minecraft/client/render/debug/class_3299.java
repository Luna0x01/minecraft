package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class class_3299 implements DebugRenderer.DebugRenderable {
	private final MinecraftClient client;

	public class_3299(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(float tickDelta, long limitTime) {
		PlayerEntity playerEntity = this.client.player;
		double d = playerEntity.prevTickX + (playerEntity.x - playerEntity.prevTickX) * (double)tickDelta;
		double e = playerEntity.prevTickY + (playerEntity.y - playerEntity.prevTickY) * (double)tickDelta;
		double f = playerEntity.prevTickZ + (playerEntity.z - playerEntity.prevTickZ) * (double)tickDelta;
		World world = this.client.player.world;
		Iterable<BlockPos> iterable = BlockPos.iterate(
			MathHelper.floor(playerEntity.x - 6.0),
			MathHelper.floor(playerEntity.y - 6.0),
			MathHelper.floor(playerEntity.z - 6.0),
			MathHelper.floor(playerEntity.x + 6.0),
			MathHelper.floor(playerEntity.y + 6.0),
			MathHelper.floor(playerEntity.z + 6.0)
		);
		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.method_12304(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);

		for (BlockPos blockPos : iterable) {
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.getBlock() != Blocks.AIR) {
				Box box = blockState.method_11722(world, blockPos).expand(0.002).offset(-d, -e, -f);
				double g = box.minX;
				double h = box.minY;
				double i = box.minZ;
				double j = box.maxX;
				double k = box.maxY;
				double l = box.maxZ;
				float m = 1.0F;
				float n = 0.0F;
				float o = 0.0F;
				float p = 0.5F;
				if (blockState.getRenderLayer(world, blockPos, Direction.WEST) == BlockRenderLayer.SOLID) {
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBuffer();
					bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
					bufferBuilder.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder.vertex(g, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder.vertex(g, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					tessellator.draw();
				}

				if (blockState.getRenderLayer(world, blockPos, Direction.SOUTH) == BlockRenderLayer.SOLID) {
					Tessellator tessellator2 = Tessellator.getInstance();
					BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
					bufferBuilder2.begin(5, VertexFormats.POSITION_COLOR);
					bufferBuilder2.vertex(g, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder2.vertex(g, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder2.vertex(j, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder2.vertex(j, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					tessellator2.draw();
				}

				if (blockState.getRenderLayer(world, blockPos, Direction.EAST) == BlockRenderLayer.SOLID) {
					Tessellator tessellator3 = Tessellator.getInstance();
					BufferBuilder bufferBuilder3 = tessellator3.getBuffer();
					bufferBuilder3.begin(5, VertexFormats.POSITION_COLOR);
					bufferBuilder3.vertex(j, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder3.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder3.vertex(j, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder3.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					tessellator3.draw();
				}

				if (blockState.getRenderLayer(world, blockPos, Direction.NORTH) == BlockRenderLayer.SOLID) {
					Tessellator tessellator4 = Tessellator.getInstance();
					BufferBuilder bufferBuilder4 = tessellator4.getBuffer();
					bufferBuilder4.begin(5, VertexFormats.POSITION_COLOR);
					bufferBuilder4.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder4.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder4.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder4.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					tessellator4.draw();
				}

				if (blockState.getRenderLayer(world, blockPos, Direction.DOWN) == BlockRenderLayer.SOLID) {
					Tessellator tessellator5 = Tessellator.getInstance();
					BufferBuilder bufferBuilder5 = tessellator5.getBuffer();
					bufferBuilder5.begin(5, VertexFormats.POSITION_COLOR);
					bufferBuilder5.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder5.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder5.vertex(g, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder5.vertex(j, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					tessellator5.draw();
				}

				if (blockState.getRenderLayer(world, blockPos, Direction.UP) == BlockRenderLayer.SOLID) {
					Tessellator tessellator6 = Tessellator.getInstance();
					BufferBuilder bufferBuilder6 = tessellator6.getBuffer();
					bufferBuilder6.begin(5, VertexFormats.POSITION_COLOR);
					bufferBuilder6.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder6.vertex(g, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder6.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					bufferBuilder6.vertex(j, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
					tessellator6.draw();
				}
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
