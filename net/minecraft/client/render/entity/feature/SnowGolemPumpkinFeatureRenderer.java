package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.SnowGolemEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;

public class SnowGolemPumpkinFeatureRenderer implements FeatureRenderer<SnowGolemEntity> {
	private final SnowGolemEntityRenderer snowGolemRenderer;

	public SnowGolemPumpkinFeatureRenderer(SnowGolemEntityRenderer snowGolemEntityRenderer) {
		this.snowGolemRenderer = snowGolemEntityRenderer;
	}

	public void render(SnowGolemEntity snowGolemEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!snowGolemEntity.isInvisible()) {
			GlStateManager.pushMatrix();
			this.snowGolemRenderer.getModel().field_1532.preRender(0.0625F);
			float m = 0.625F;
			GlStateManager.translate(0.0F, -0.34375F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(m, -m, -m);
			MinecraftClient.getInstance().getHeldItemRenderer().renderItem(snowGolemEntity, new ItemStack(Blocks.PUMPKIN, 1), ModelTransformation.Mode.HEAD);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean combineTextures() {
		return true;
	}
}
