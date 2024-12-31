package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PistonBlockEntityRenderer extends BlockEntityRenderer<PistonBlockEntity> {
	private final BlockRenderManager renderManager = MinecraftClient.getInstance().getBlockRenderManager();

	public void render(PistonBlockEntity pistonBlockEntity, double d, double e, double f, float g, int i) {
		BlockPos blockPos = pistonBlockEntity.getPos();
		BlockState blockState = pistonBlockEntity.getPushedBlock();
		Block block = blockState.getBlock();
		if (block.getMaterial() != Material.AIR && !(pistonBlockEntity.getAmountExtended(g) >= 1.0F)) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			DiffuseLighting.disable();
			GlStateManager.blendFunc(770, 771);
			GlStateManager.enableBlend();
			GlStateManager.disableCull();
			if (MinecraftClient.isAmbientOcclusionEnabled()) {
				GlStateManager.shadeModel(7425);
			} else {
				GlStateManager.shadeModel(7424);
			}

			bufferBuilder.begin(7, VertexFormats.BLOCK);
			bufferBuilder.offset(
				(double)((float)d - (float)blockPos.getX() + pistonBlockEntity.getRenderOffsetX(g)),
				(double)((float)e - (float)blockPos.getY() + pistonBlockEntity.getRenderOffsetY(g)),
				(double)((float)f - (float)blockPos.getZ() + pistonBlockEntity.getRenderOffsetZ(g))
			);
			World world = this.getWorld();
			if (block == Blocks.PISTON_HEAD && pistonBlockEntity.getAmountExtended(g) < 0.5F) {
				blockState = blockState.with(PistonHeadBlock.SHORT, true);
				this.renderManager.getModelRenderer().render(world, this.renderManager.getModel(blockState, world, blockPos), blockState, blockPos, bufferBuilder, true);
			} else if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
				PistonHeadBlock.PistonHeadType pistonHeadType = block == Blocks.STICKY_PISTON
					? PistonHeadBlock.PistonHeadType.STICKY
					: PistonHeadBlock.PistonHeadType.DEFAULT;
				BlockState blockState2 = Blocks.PISTON_HEAD
					.getDefaultState()
					.with(PistonHeadBlock.TYPE, pistonHeadType)
					.with(PistonHeadBlock.FACING, blockState.get(PistonBlock.DIRECTION));
				blockState2 = blockState2.with(PistonHeadBlock.SHORT, pistonBlockEntity.getAmountExtended(g) >= 0.5F);
				this.renderManager.getModelRenderer().render(world, this.renderManager.getModel(blockState2, world, blockPos), blockState2, blockPos, bufferBuilder, true);
				bufferBuilder.offset((double)((float)d - (float)blockPos.getX()), (double)((float)e - (float)blockPos.getY()), (double)((float)f - (float)blockPos.getZ()));
				blockState.with(PistonBlock.EXTENDED, true);
				this.renderManager.getModelRenderer().render(world, this.renderManager.getModel(blockState, world, blockPos), blockState, blockPos, bufferBuilder, true);
			} else {
				this.renderManager.getModelRenderer().render(world, this.renderManager.getModel(blockState, world, blockPos), blockState, blockPos, bufferBuilder, false);
			}

			bufferBuilder.offset(0.0, 0.0, 0.0);
			tessellator.draw();
			DiffuseLighting.enableNormally();
		}
	}
}
