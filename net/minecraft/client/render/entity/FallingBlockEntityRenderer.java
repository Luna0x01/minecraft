package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FallingBlockEntityRenderer extends EntityRenderer<FallingBlockEntity> {
	public FallingBlockEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.shadowSize = 0.5F;
	}

	public void render(FallingBlockEntity fallingBlockEntity, double d, double e, double f, float g, float h) {
		if (fallingBlockEntity.getBlockState() != null) {
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			BlockState blockState = fallingBlockEntity.getBlockState();
			Block block = blockState.getBlock();
			BlockPos blockPos = new BlockPos(fallingBlockEntity);
			World world = fallingBlockEntity.method_3056();
			if (blockState != world.getBlockState(blockPos) && block.getBlockType() != -1) {
				if (block.getBlockType() == 3) {
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)d, (float)e, (float)f);
					GlStateManager.disableLighting();
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBuffer();
					bufferBuilder.begin(7, VertexFormats.BLOCK);
					int i = blockPos.getX();
					int j = blockPos.getY();
					int k = blockPos.getZ();
					bufferBuilder.offset((double)((float)(-i) - 0.5F), (double)(-j), (double)((float)(-k) - 0.5F));
					BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
					BakedModel bakedModel = blockRenderManager.getModel(blockState, world, null);
					blockRenderManager.getModelRenderer().render(world, bakedModel, blockState, blockPos, bufferBuilder, false);
					bufferBuilder.offset(0.0, 0.0, 0.0);
					tessellator.draw();
					GlStateManager.enableLighting();
					GlStateManager.popMatrix();
					super.render(fallingBlockEntity, d, e, f, g, h);
				}
			}
		}
	}

	protected Identifier getTexture(FallingBlockEntity fallingBlockEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
