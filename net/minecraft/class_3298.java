package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.render.block.model.BedBlockModel;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class class_3298 extends class_4239<BedBlockEntity> {
	private static final Identifier[] TEXTURES = (Identifier[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(dyeColor -> new Identifier("textures/entity/bed/" + dyeColor.getTranslationKey() + ".png"))
		.toArray(Identifier[]::new);
	private final BedBlockModel field_20826 = new BedBlockModel();

	public void method_1631(BedBlockEntity bedBlockEntity, double d, double e, double f, float g, int i) {
		if (i >= 0) {
			this.method_19327(field_20846[i]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			Identifier identifier = TEXTURES[bedBlockEntity.getColor().getId()];
			if (identifier != null) {
				this.method_19327(identifier);
			}
		}

		if (bedBlockEntity.hasWorld()) {
			BlockState blockState = bedBlockEntity.method_16783();
			this.method_19287(blockState.getProperty(BedBlock.PART) == BedPart.HEAD, d, e, f, blockState.getProperty(BedBlock.FACING));
		} else {
			this.method_19287(true, d, e, f, Direction.SOUTH);
			this.method_19287(false, d, e, f - 1.0, Direction.SOUTH);
		}

		if (i >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}

	private void method_19287(boolean bl, double d, double e, double f, Direction direction) {
		this.field_20826.method_14645(bl);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d, (float)e + 0.5625F, (float)f);
		GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		GlStateManager.rotate(180.0F + direction.method_12578(), 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		GlStateManager.enableRescaleNormal();
		this.field_20826.method_14646();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
}
