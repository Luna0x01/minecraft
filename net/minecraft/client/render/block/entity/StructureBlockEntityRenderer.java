package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureBlockEntityRenderer extends BlockEntityRenderer<StructureBlockEntity> {
	public void render(StructureBlockEntity structureBlockEntity, double d, double e, double f, float g, int i, float h) {
		if (MinecraftClient.getInstance().player.method_13567() || MinecraftClient.getInstance().player.isSpectator()) {
			super.render(structureBlockEntity, d, e, f, g, i, h);
			BlockPos blockPos = structureBlockEntity.method_13347();
			BlockPos blockPos2 = structureBlockEntity.method_13350();
			if (blockPos2.getX() >= 1 && blockPos2.getY() >= 1 && blockPos2.getZ() >= 1) {
				if (structureBlockEntity.method_13354() == StructureBlockEntity.class_2739.SAVE
					|| structureBlockEntity.method_13354() == StructureBlockEntity.class_2739.LOAD) {
					double j = 0.01;
					double k = (double)blockPos.getX();
					double l = (double)blockPos.getZ();
					double m = e + (double)blockPos.getY() - 0.01;
					double n = m + (double)blockPos2.getY() + 0.02;
					double o;
					double p;
					switch (structureBlockEntity.method_13351()) {
						case LEFT_RIGHT:
							o = (double)blockPos2.getX() + 0.02;
							p = -((double)blockPos2.getZ() + 0.02);
							break;
						case FRONT_BACK:
							o = -((double)blockPos2.getX() + 0.02);
							p = (double)blockPos2.getZ() + 0.02;
							break;
						default:
							o = (double)blockPos2.getX() + 0.02;
							p = (double)blockPos2.getZ() + 0.02;
					}

					double ag;
					double ah;
					double ai;
					double aj;
					switch (structureBlockEntity.method_13352()) {
						case CLOCKWISE_90:
							ag = d + (p < 0.0 ? k - 0.01 : k + 1.0 + 0.01);
							ah = f + (o < 0.0 ? l + 1.0 + 0.01 : l - 0.01);
							ai = ag - p;
							aj = ah + o;
							break;
						case CLOCKWISE_180:
							ag = d + (o < 0.0 ? k - 0.01 : k + 1.0 + 0.01);
							ah = f + (p < 0.0 ? l - 0.01 : l + 1.0 + 0.01);
							ai = ag - o;
							aj = ah - p;
							break;
						case COUNTERCLOCKWISE_90:
							ag = d + (p < 0.0 ? k + 1.0 + 0.01 : k - 0.01);
							ah = f + (o < 0.0 ? l - 0.01 : l + 1.0 + 0.01);
							ai = ag + p;
							aj = ah - o;
							break;
						default:
							ag = d + (o < 0.0 ? k + 1.0 + 0.01 : k - 0.01);
							ah = f + (p < 0.0 ? l + 1.0 + 0.01 : l - 0.01);
							ai = ag + o;
							aj = ah + p;
					}

					int ak = 255;
					int al = 223;
					int am = 127;
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBuffer();
					GlStateManager.disableFog();
					GlStateManager.disableLighting();
					GlStateManager.disableTexture();
					GlStateManager.enableBlend();
					GlStateManager.method_12288(
						GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
					);
					this.method_13445(true);
					if (structureBlockEntity.method_13354() == StructureBlockEntity.class_2739.SAVE || structureBlockEntity.method_13336()) {
						this.method_13447(tessellator, bufferBuilder, ag, m, ah, ai, n, aj, 255, 223, 127);
					}

					if (structureBlockEntity.method_13354() == StructureBlockEntity.class_2739.SAVE && structureBlockEntity.method_13335()) {
						this.method_13446(structureBlockEntity, d, e, f, blockPos, tessellator, bufferBuilder, true);
						this.method_13446(structureBlockEntity, d, e, f, blockPos, tessellator, bufferBuilder, false);
					}

					this.method_13445(false);
					GlStateManager.method_12304(1.0F);
					GlStateManager.enableLighting();
					GlStateManager.enableTexture();
					GlStateManager.enableDepthTest();
					GlStateManager.depthMask(true);
					GlStateManager.enableFog();
				}
			}
		}
	}

	private void method_13446(
		StructureBlockEntity structureBlockEntity, double d, double e, double f, BlockPos blockPos, Tessellator tessellator, BufferBuilder bufferBuilder, boolean bl
	) {
		GlStateManager.method_12304(bl ? 3.0F : 1.0F);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
		World world = structureBlockEntity.getEntityWorld();
		BlockPos blockPos2 = structureBlockEntity.getPos();
		BlockPos blockPos3 = blockPos2.add(blockPos);

		for (BlockPos blockPos4 : BlockPos.iterate(blockPos3, blockPos3.add(structureBlockEntity.method_13350()).add(-1, -1, -1))) {
			BlockState blockState = world.getBlockState(blockPos4);
			boolean bl2 = blockState == Blocks.AIR.getDefaultState();
			boolean bl3 = blockState == Blocks.STRUCTURE_VOID.getDefaultState();
			if (bl2 || bl3) {
				float g = bl2 ? 0.05F : 0.0F;
				double h = (double)((float)(blockPos4.getX() - blockPos2.getX()) + 0.45F) + d - (double)g;
				double i = (double)((float)(blockPos4.getY() - blockPos2.getY()) + 0.45F) + e - (double)g;
				double j = (double)((float)(blockPos4.getZ() - blockPos2.getZ()) + 0.45F) + f - (double)g;
				double k = (double)((float)(blockPos4.getX() - blockPos2.getX()) + 0.55F) + d + (double)g;
				double l = (double)((float)(blockPos4.getY() - blockPos2.getY()) + 0.55F) + e + (double)g;
				double m = (double)((float)(blockPos4.getZ() - blockPos2.getZ()) + 0.55F) + f + (double)g;
				if (bl) {
					WorldRenderer.method_13431(bufferBuilder, h, i, j, k, l, m, 0.0F, 0.0F, 0.0F, 1.0F);
				} else if (bl2) {
					WorldRenderer.method_13431(bufferBuilder, h, i, j, k, l, m, 0.5F, 0.5F, 1.0F, 1.0F);
				} else {
					WorldRenderer.method_13431(bufferBuilder, h, i, j, k, l, m, 1.0F, 0.25F, 0.25F, 1.0F);
				}
			}
		}

		tessellator.draw();
	}

	private void method_13447(
		Tessellator tessellator, BufferBuilder bufferBuilder, double d, double e, double f, double g, double h, double i, int j, int k, int l
	) {
		GlStateManager.method_12304(2.0F);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(d, e, f).color((float)k, (float)k, (float)k, 0.0F).next();
		bufferBuilder.vertex(d, e, f).color(k, k, k, j).next();
		bufferBuilder.vertex(g, e, f).color(k, l, l, j).next();
		bufferBuilder.vertex(g, e, i).color(k, k, k, j).next();
		bufferBuilder.vertex(d, e, i).color(k, k, k, j).next();
		bufferBuilder.vertex(d, e, f).color(l, l, k, j).next();
		bufferBuilder.vertex(d, h, f).color(l, k, l, j).next();
		bufferBuilder.vertex(g, h, f).color(k, k, k, j).next();
		bufferBuilder.vertex(g, h, i).color(k, k, k, j).next();
		bufferBuilder.vertex(d, h, i).color(k, k, k, j).next();
		bufferBuilder.vertex(d, h, f).color(k, k, k, j).next();
		bufferBuilder.vertex(d, h, i).color(k, k, k, j).next();
		bufferBuilder.vertex(d, e, i).color(k, k, k, j).next();
		bufferBuilder.vertex(g, e, i).color(k, k, k, j).next();
		bufferBuilder.vertex(g, h, i).color(k, k, k, j).next();
		bufferBuilder.vertex(g, h, f).color(k, k, k, j).next();
		bufferBuilder.vertex(g, e, f).color(k, k, k, j).next();
		bufferBuilder.vertex(g, e, f).color((float)k, (float)k, (float)k, 0.0F).next();
		tessellator.draw();
		GlStateManager.method_12304(1.0F);
	}

	public boolean method_12410(StructureBlockEntity structureBlockEntity) {
		return true;
	}
}
