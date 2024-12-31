package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockRenderManager;
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
		BlockState blockState = fallingBlockEntity.getBlockState();
		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			World world = fallingBlockEntity.method_3056();
			if (blockState != world.getBlockState(new BlockPos(fallingBlockEntity)) && blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				if (this.field_13631) {
					GlStateManager.enableColorMaterial();
					GlStateManager.method_12309(this.method_12454(fallingBlockEntity));
				}

				bufferBuilder.begin(7, VertexFormats.BLOCK);
				BlockPos blockPos = new BlockPos(fallingBlockEntity.x, fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.z);
				GlStateManager.translate((float)(d - (double)blockPos.getX() - 0.5), (float)(e - (double)blockPos.getY()), (float)(f - (double)blockPos.getZ() - 0.5));
				BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
				blockRenderManager.getModelRenderer()
					.method_19197(
						world,
						blockRenderManager.method_12346(blockState),
						blockState,
						blockPos,
						bufferBuilder,
						false,
						new Random(),
						blockState.getRenderingSeed(fallingBlockEntity.getFallingBlockPos())
					);
				tessellator.draw();
				if (this.field_13631) {
					GlStateManager.method_12315();
					GlStateManager.disableColorMaterial();
				}

				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
				super.render(fallingBlockEntity, d, e, f, g, h);
			}
		}
	}

	protected Identifier getTexture(FallingBlockEntity fallingBlockEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
