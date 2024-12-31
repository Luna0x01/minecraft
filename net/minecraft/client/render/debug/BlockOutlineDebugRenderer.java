package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockOutlineDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;

	public BlockOutlineDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		BlockView blockView = this.client.player.world;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.lineWidth(2.0F);
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);
		BlockPos blockPos = new BlockPos(cameraX, cameraY, cameraZ);

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-6, -6, -6), blockPos.add(6, 6, 6))) {
			BlockState blockState = blockView.getBlockState(blockPos2);
			if (!blockState.isOf(Blocks.AIR)) {
				VoxelShape voxelShape = blockState.getOutlineShape(blockView, blockPos2);

				for (Box box : voxelShape.getBoundingBoxes()) {
					Box box2 = box.offset(blockPos2).expand(0.002).offset(-cameraX, -cameraY, -cameraZ);
					double d = box2.minX;
					double e = box2.minY;
					double f = box2.minZ;
					double g = box2.maxX;
					double h = box2.maxY;
					double i = box2.maxZ;
					float j = 1.0F;
					float k = 0.0F;
					float l = 0.0F;
					float m = 0.5F;
					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.WEST)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBuffer();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(d, e, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(d, e, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(d, h, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(d, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.SOUTH)) {
						Tessellator tessellator2 = Tessellator.getInstance();
						BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
						bufferBuilder2.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder2.vertex(d, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder2.vertex(d, e, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder2.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder2.vertex(g, e, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator2.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.EAST)) {
						Tessellator tessellator3 = Tessellator.getInstance();
						BufferBuilder bufferBuilder3 = tessellator3.getBuffer();
						bufferBuilder3.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder3.vertex(g, e, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder3.vertex(g, e, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder3.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder3.vertex(g, h, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator3.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.NORTH)) {
						Tessellator tessellator4 = Tessellator.getInstance();
						BufferBuilder bufferBuilder4 = tessellator4.getBuffer();
						bufferBuilder4.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder4.vertex(g, h, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder4.vertex(g, e, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder4.vertex(d, h, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder4.vertex(d, e, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator4.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.DOWN)) {
						Tessellator tessellator5 = Tessellator.getInstance();
						BufferBuilder bufferBuilder5 = tessellator5.getBuffer();
						bufferBuilder5.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder5.vertex(d, e, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder5.vertex(g, e, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder5.vertex(d, e, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder5.vertex(g, e, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator5.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.UP)) {
						Tessellator tessellator6 = Tessellator.getInstance();
						BufferBuilder bufferBuilder6 = tessellator6.getBuffer();
						bufferBuilder6.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder6.vertex(d, h, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder6.vertex(d, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder6.vertex(g, h, f).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder6.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator6.draw();
					}
				}
			}
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}
