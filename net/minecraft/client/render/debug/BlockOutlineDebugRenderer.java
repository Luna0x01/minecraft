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
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f) {
		BlockView blockView = this.client.player.world;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.lineWidth(2.0F);
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);
		BlockPos blockPos = new BlockPos(d, e, f);

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-6, -6, -6), blockPos.add(6, 6, 6))) {
			BlockState blockState = blockView.getBlockState(blockPos2);
			if (blockState.getBlock() != Blocks.field_10124) {
				VoxelShape voxelShape = blockState.getOutlineShape(blockView, blockPos2);

				for (Box box : voxelShape.getBoundingBoxes()) {
					Box box2 = box.offset(blockPos2).expand(0.002).offset(-d, -e, -f);
					double g = box2.x1;
					double h = box2.y1;
					double i = box2.z1;
					double j = box2.x2;
					double k = box2.y2;
					double l = box2.z2;
					float m = 1.0F;
					float n = 0.0F;
					float o = 0.0F;
					float p = 0.5F;
					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11039)) {
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferBuilder = tessellator.getBuffer();
						bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder.vertex(g, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11035)) {
						Tessellator tessellator2 = Tessellator.getInstance();
						BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
						bufferBuilder2.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder2.vertex(g, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder2.vertex(g, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder2.vertex(j, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder2.vertex(j, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator2.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11034)) {
						Tessellator tessellator3 = Tessellator.getInstance();
						BufferBuilder bufferBuilder3 = tessellator3.getBuffer();
						bufferBuilder3.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder3.vertex(j, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder3.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder3.vertex(j, k, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder3.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator3.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11043)) {
						Tessellator tessellator4 = Tessellator.getInstance();
						BufferBuilder bufferBuilder4 = tessellator4.getBuffer();
						bufferBuilder4.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder4.vertex(j, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder4.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder4.vertex(g, k, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder4.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator4.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11033)) {
						Tessellator tessellator5 = Tessellator.getInstance();
						BufferBuilder bufferBuilder5 = tessellator5.getBuffer();
						bufferBuilder5.begin(5, VertexFormats.POSITION_COLOR);
						bufferBuilder5.vertex(g, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder5.vertex(j, h, i).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder5.vertex(g, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						bufferBuilder5.vertex(j, h, l).color(1.0F, 0.0F, 0.0F, 0.5F).next();
						tessellator5.draw();
					}

					if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11036)) {
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
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}
