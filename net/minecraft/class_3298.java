package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.model.BedBlockModel;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class class_3298 extends BlockEntityRenderer<BedBlockEntity> {
	private static final Identifier[] TEXTURES;
	private BedBlockModel model = new BedBlockModel();
	private int field_16139 = this.model.method_14644();

	public void render(BedBlockEntity bedBlockEntity, double d, double e, double f, float g, int i, float h) {
		if (this.field_16139 != this.model.method_14644()) {
			this.model = new BedBlockModel();
			this.field_16139 = this.model.method_14644();
		}

		boolean bl = bedBlockEntity.getEntityWorld() != null;
		boolean bl2 = bl ? bedBlockEntity.method_14366() : true;
		DyeColor dyeColor = bedBlockEntity != null ? bedBlockEntity.getColor() : DyeColor.RED;
		int j = bl ? bedBlockEntity.getDataValue() & 3 : 0;
		if (i >= 0) {
			this.bindTexture(DESTROY_STAGE_TEXTURE[i]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			Identifier identifier = TEXTURES[dyeColor.getId()];
			if (identifier != null) {
				this.bindTexture(identifier);
			}
		}

		if (bl) {
			this.method_14684(bl2, d, e, f, j, h);
		} else {
			GlStateManager.pushMatrix();
			this.method_14684(true, d, e, f, j, h);
			this.method_14684(false, d, e, f - 1.0, j, h);
			GlStateManager.popMatrix();
		}

		if (i >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}

	private void method_14684(boolean bl, double d, double e, double f, int i, float g) {
		this.model.method_14645(bl);
		GlStateManager.pushMatrix();
		float h = 0.0F;
		float j = 0.0F;
		float k = 0.0F;
		if (i == Direction.NORTH.getHorizontal()) {
			h = 0.0F;
		} else if (i == Direction.SOUTH.getHorizontal()) {
			h = 180.0F;
			j = 1.0F;
			k = 1.0F;
		} else if (i == Direction.WEST.getHorizontal()) {
			h = -90.0F;
			k = 1.0F;
		} else if (i == Direction.EAST.getHorizontal()) {
			h = 90.0F;
			j = 1.0F;
		}

		GlStateManager.translate((float)d + j, (float)e + 0.5625F, (float)f + k);
		GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(h, 0.0F, 0.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		this.model.method_14646();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, g);
		GlStateManager.popMatrix();
	}

	static {
		DyeColor[] dyeColors = DyeColor.values();
		TEXTURES = new Identifier[dyeColors.length];

		for (DyeColor dyeColor : dyeColors) {
			TEXTURES[dyeColor.getId()] = new Identifier("textures/entity/bed/" + dyeColor.getName() + ".png");
		}
	}
}
