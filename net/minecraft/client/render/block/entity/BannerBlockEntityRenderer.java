package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.class_3733;
import net.minecraft.class_4239;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.class_2871;
import net.minecraft.client.render.entity.model.BannerBlockEntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class BannerBlockEntityRenderer extends class_4239<BannerBlockEntity> {
	private final BannerBlockEntityModel model = new BannerBlockEntityModel();

	public void method_1631(BannerBlockEntity bannerBlockEntity, double d, double e, double f, float g, int i) {
		float h = 0.6666667F;
		boolean bl = bannerBlockEntity.getEntityWorld() == null;
		GlStateManager.pushMatrix();
		ModelPart modelPart = this.model.method_18902();
		long l;
		if (bl) {
			l = 0L;
			GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			modelPart.visible = true;
		} else {
			l = bannerBlockEntity.getEntityWorld().getLastUpdateTime();
			BlockState blockState = bannerBlockEntity.method_16783();
			if (blockState.getBlock() instanceof BannerBlock) {
				GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
				GlStateManager.rotate((float)(-(Integer)blockState.getProperty(BannerBlock.ROTATION) * 360) / 16.0F, 0.0F, 1.0F, 0.0F);
				modelPart.visible = true;
			} else {
				GlStateManager.translate((float)d + 0.5F, (float)e - 0.16666667F, (float)f + 0.5F);
				GlStateManager.rotate(-((Direction)blockState.getProperty(class_3733.field_18574)).method_12578(), 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
				modelPart.visible = false;
			}
		}

		BlockPos blockPos = bannerBlockEntity.getPos();
		float j = (float)((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l) + g;
		this.model.method_18903().posX = (-0.0125F + 0.01F * MathHelper.cos(j * (float) Math.PI * 0.02F)) * (float) Math.PI;
		GlStateManager.enableRescaleNormal();
		Identifier identifier = this.getTexture(bannerBlockEntity);
		if (identifier != null) {
			this.method_19327(identifier);
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
			this.model.render();
			GlStateManager.popMatrix();
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	@Nullable
	private Identifier getTexture(BannerBlockEntity blockEntity) {
		return class_2871.field_13540.method_12344(blockEntity.getTextureIdentifier(), blockEntity.getPatterns(), blockEntity.getColors());
	}
}
