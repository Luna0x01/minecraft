package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_4239;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class StructureBlockEntityRenderer extends class_4239<StructureBlockEntity> {
	public void method_1631(StructureBlockEntity structureBlockEntity, double d, double e, double f, float g, int i) {
		if (MinecraftClient.getInstance().player.method_15936() || MinecraftClient.getInstance().player.isSpectator()) {
			super.method_1631(structureBlockEntity, d, e, f, g, i);
			BlockPos blockPos = structureBlockEntity.method_13347();
			BlockPos blockPos2 = structureBlockEntity.method_13350();
			if (blockPos2.getX() >= 1 && blockPos2.getY() >= 1 && blockPos2.getZ() >= 1) {
				if (structureBlockEntity.method_13354() == StructureBlockMode.SAVE || structureBlockEntity.method_13354() == StructureBlockMode.LOAD) {
					double h = 0.01;
					double j = (double)blockPos.getX();
					double k = (double)blockPos.getZ();
					double l = e + (double)blockPos.getY() - 0.01;
					double m = l + (double)blockPos2.getY() + 0.02;
					double n;
					double o;
					switch (structureBlockEntity.method_13351()) {
						case LEFT_RIGHT:
							n = (double)blockPos2.getX() + 0.02;
							o = -((double)blockPos2.getZ() + 0.02);
							break;
						case FRONT_BACK:
							n = -((double)blockPos2.getX() + 0.02);
							o = (double)blockPos2.getZ() + 0.02;
							break;
						default:
							n = (double)blockPos2.getX() + 0.02;
							o = (double)blockPos2.getZ() + 0.02;
					}

					double af;
					double ag;
					double ah;
					double ai;
					switch (structureBlockEntity.method_13352()) {
						case CLOCKWISE_90:
							af = d + (o < 0.0 ? j - 0.01 : j + 1.0 + 0.01);
							ag = f + (n < 0.0 ? k + 1.0 + 0.01 : k - 0.01);
							ah = af - o;
							ai = ag + n;
							break;
						case CLOCKWISE_180:
							af = d + (n < 0.0 ? j - 0.01 : j + 1.0 + 0.01);
							ag = f + (o < 0.0 ? k - 0.01 : k + 1.0 + 0.01);
							ah = af - n;
							ai = ag - o;
							break;
						case COUNTERCLOCKWISE_90:
							af = d + (o < 0.0 ? j + 1.0 + 0.01 : j - 0.01);
							ag = f + (n < 0.0 ? k - 0.01 : k + 1.0 + 0.01);
							ah = af + o;
							ai = ag - n;
							break;
						default:
							af = d + (n < 0.0 ? j + 1.0 + 0.01 : j - 0.01);
							ag = f + (o < 0.0 ? k + 1.0 + 0.01 : k - 0.01);
							ah = af + n;
							ai = ag + o;
					}

					int aj = 255;
					int ak = 223;
					int al = 127;
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBuffer();
					GlStateManager.disableFog();
					GlStateManager.disableLighting();
					GlStateManager.disableTexture();
					GlStateManager.enableBlend();
					GlStateManager.method_12288(
						GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
					);
					this.method_19328(true);
					if (structureBlockEntity.method_13354() == StructureBlockMode.SAVE || structureBlockEntity.method_13336()) {
						this.method_13447(tessellator, bufferBuilder, af, l, ag, ah, m, ai, 255, 223, 127);
					}

					if (structureBlockEntity.method_13354() == StructureBlockMode.SAVE && structureBlockEntity.method_13335()) {
						this.method_13446(structureBlockEntity, d, e, f, blockPos, tessellator, bufferBuilder, true);
						this.method_13446(structureBlockEntity, d, e, f, blockPos, tessellator, bufferBuilder, false);
					}

					this.method_19328(false);
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
		BlockView blockView = structureBlockEntity.getEntityWorld();
		BlockPos blockPos2 = structureBlockEntity.getPos();
		BlockPos blockPos3 = blockPos2.add(blockPos);

		for (BlockPos blockPos4 : BlockPos.iterate(blockPos3, blockPos3.add(structureBlockEntity.method_13350()).add(-1, -1, -1))) {
			BlockState blockState = blockView.getBlockState(blockPos4);
			boolean bl2 = blockState.isAir();
			boolean bl3 = blockState.getBlock() == Blocks.STRUCTURE_VOID;
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
