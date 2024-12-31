package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.class_4239;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PistonBlockEntityRenderer extends class_4239<PistonBlockEntity> {
	private final BlockRenderManager renderManager = MinecraftClient.getInstance().getBlockRenderManager();

	public void method_1631(PistonBlockEntity pistonBlockEntity, double d, double e, double f, float g, int i) {
		BlockPos blockPos = pistonBlockEntity.getPos().offset(pistonBlockEntity.method_16854().getOpposite());
		BlockState blockState = pistonBlockEntity.getPushedBlock();
		if (!blockState.isAir() && !(pistonBlockEntity.getAmountExtended(g) >= 1.0F)) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			this.method_19327(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			DiffuseLighting.disable();
			GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.disableCull();
			if (MinecraftClient.isAmbientOcclusionEnabled()) {
				GlStateManager.shadeModel(7425);
			} else {
				GlStateManager.shadeModel(7424);
			}

			bufferBuilder.begin(7, VertexFormats.BLOCK);
			bufferBuilder.offset(
				d - (double)blockPos.getX() + (double)pistonBlockEntity.getRenderOffsetX(g),
				e - (double)blockPos.getY() + (double)pistonBlockEntity.getRenderOffsetY(g),
				f - (double)blockPos.getZ() + (double)pistonBlockEntity.getRenderOffsetZ(g)
			);
			World world = this.method_19325();
			if (blockState.getBlock() == Blocks.PISTON_HEAD && pistonBlockEntity.getAmountExtended(g) <= 4.0F) {
				blockState = blockState.withProperty(PistonHeadBlock.field_18668, Boolean.valueOf(true));
				this.method_12411(blockPos, blockState, bufferBuilder, world, false);
			} else if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
				PistonType pistonType = blockState.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState blockState2 = Blocks.PISTON_HEAD
					.getDefaultState()
					.withProperty(PistonHeadBlock.field_18667, pistonType)
					.withProperty(PistonHeadBlock.FACING, blockState.getProperty(PistonBlock.FACING));
				blockState2 = blockState2.withProperty(PistonHeadBlock.field_18668, Boolean.valueOf(pistonBlockEntity.getAmountExtended(g) >= 0.5F));
				this.method_12411(blockPos, blockState2, bufferBuilder, world, false);
				BlockPos blockPos2 = blockPos.offset(pistonBlockEntity.method_16854());
				bufferBuilder.offset(d - (double)blockPos2.getX(), e - (double)blockPos2.getY(), f - (double)blockPos2.getZ());
				blockState = blockState.withProperty(PistonBlock.field_18654, Boolean.valueOf(true));
				this.method_12411(blockPos2, blockState, bufferBuilder, world, true);
			} else {
				this.method_12411(blockPos, blockState, bufferBuilder, world, false);
			}

			bufferBuilder.offset(0.0, 0.0, 0.0);
			tessellator.draw();
			DiffuseLighting.enableNormally();
		}
	}

	private boolean method_12411(BlockPos blockPos, BlockState blockState, BufferBuilder bufferBuilder, World world, boolean bl) {
		return this.renderManager
			.getModelRenderer()
			.method_19197(
				world, this.renderManager.method_12346(blockState), blockState, blockPos, bufferBuilder, bl, new Random(), blockState.getRenderingSeed(blockPos)
			);
	}
}
