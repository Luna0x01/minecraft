package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.client.render.entity.model.ChestBlockEntityModel;
import net.minecraft.util.Identifier;

public class EnderChestBlockEntityRenderer extends BlockEntityRenderer<EnderChestBlockEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/chest/ender.png");
	private ChestBlockEntityModel model = new ChestBlockEntityModel();

	public void render(EnderChestBlockEntity enderChestBlockEntity, double d, double e, double f, float g, int i) {
		int j = 0;
		if (enderChestBlockEntity.hasWorld()) {
			j = enderChestBlockEntity.getDataValue();
		}

		if (i >= 0) {
			this.bindTexture(DESTROY_STAGE_TEXTURE[i]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			this.bindTexture(TEXTURE);
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate((float)d, (float)e + 1.0F, (float)f + 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		int k = 0;
		if (j == 2) {
			k = 180;
		}

		if (j == 3) {
			k = 0;
		}

		if (j == 4) {
			k = 90;
		}

		if (j == 5) {
			k = -90;
		}

		GlStateManager.rotate((float)k, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		float h = enderChestBlockEntity.lastAnimationProgress + (enderChestBlockEntity.animationProgress - enderChestBlockEntity.lastAnimationProgress) * g;
		h = 1.0F - h;
		h = 1.0F - h * h * h;
		this.model.lid.posX = -(h * (float) Math.PI / 2.0F);
		this.model.renderParts();
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
