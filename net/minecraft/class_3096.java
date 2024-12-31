package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.util.math.Direction;

public class class_3096 extends BlockEntityRenderer<ShulkerBoxBlockEntity> {
	private final ShulkerEntityModel field_15278;

	public class_3096(ShulkerEntityModel shulkerEntityModel) {
		this.field_15278 = shulkerEntityModel;
	}

	public void render(ShulkerBoxBlockEntity shulkerBoxBlockEntity, double d, double e, double f, float g, int i, float h) {
		Direction direction = Direction.UP;
		if (shulkerBoxBlockEntity.hasWorld()) {
			BlockState blockState = this.getWorld().getBlockState(shulkerBoxBlockEntity.getPos());
			if (blockState.getBlock() instanceof ShulkerBoxBlock) {
				direction = blockState.get(ShulkerBoxBlock.FACING);
			}
		}

		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		GlStateManager.disableCull();
		if (i >= 0) {
			this.bindTexture(DESTROY_STAGE_TEXTURE[i]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			this.bindTexture(ShulkerEntityRenderer.TEXTURES[shulkerBoxBlockEntity.getColor().getId()]);
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		if (i < 0) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, h);
		}

		GlStateManager.translate((float)d + 0.5F, (float)e + 1.5F, (float)f + 0.5F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.0F, 1.0F, 0.0F);
		float j = 0.9995F;
		GlStateManager.scale(0.9995F, 0.9995F, 0.9995F);
		GlStateManager.translate(0.0F, -1.0F, 0.0F);
		switch (direction) {
			case DOWN:
				GlStateManager.translate(0.0F, 2.0F, 0.0F);
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			case UP:
			default:
				break;
			case NORTH:
				GlStateManager.translate(0.0F, 1.0F, 1.0F);
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				break;
			case SOUTH:
				GlStateManager.translate(0.0F, 1.0F, -1.0F);
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				break;
			case WEST:
				GlStateManager.translate(-1.0F, 1.0F, 0.0F);
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
				break;
			case EAST:
				GlStateManager.translate(1.0F, 1.0F, 0.0F);
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		}

		this.field_15278.field_13396.render(0.0625F);
		GlStateManager.translate(0.0F, -shulkerBoxBlockEntity.method_13734(g) * 0.5F, 0.0F);
		GlStateManager.rotate(270.0F * shulkerBoxBlockEntity.method_13734(g), 0.0F, 1.0F, 0.0F);
		this.field_15278.field_13397.render(0.0625F);
		GlStateManager.enableCull();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if (i >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}
}
