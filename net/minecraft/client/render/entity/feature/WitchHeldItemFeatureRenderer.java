package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.WitchEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class WitchHeldItemFeatureRenderer implements FeatureRenderer<WitchEntity> {
	private final WitchEntityRenderer witchRenderer;

	public WitchHeldItemFeatureRenderer(WitchEntityRenderer witchEntityRenderer) {
		this.witchRenderer = witchEntityRenderer;
	}

	public void render(WitchEntity witchEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = witchEntity.getMainHandStack();
		if (!itemStack.isEmpty()) {
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			if (this.witchRenderer.getModel().child) {
				GlStateManager.translate(0.0F, 0.625F, 0.0F);
				GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
				float m = 0.5F;
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
			}

			this.witchRenderer.getModel().method_18946().preRender(0.0625F);
			GlStateManager.translate(-0.0625F, 0.53125F, 0.21875F);
			Item item = itemStack.getItem();
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			if (Block.getBlockFromItem(item).getDefaultState().getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED) {
				GlStateManager.translate(0.0F, 0.0625F, -0.25F);
				GlStateManager.rotate(30.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-5.0F, 0.0F, 1.0F, 0.0F);
				float n = 0.375F;
				GlStateManager.scale(0.375F, -0.375F, 0.375F);
			} else if (item == Items.BOW) {
				GlStateManager.translate(0.0F, 0.125F, -0.125F);
				GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
				float o = 0.625F;
				GlStateManager.scale(0.625F, -0.625F, 0.625F);
				GlStateManager.rotate(-100.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-20.0F, 0.0F, 1.0F, 0.0F);
			} else {
				GlStateManager.translate(0.1875F, 0.1875F, 0.0F);
				float p = 0.875F;
				GlStateManager.scale(0.875F, 0.875F, 0.875F);
				GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotate(-60.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-30.0F, 0.0F, 0.0F, 1.0F);
			}

			GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(40.0F, 0.0F, 0.0F, 1.0F);
			minecraftClient.method_18201().method_19139(witchEntity, itemStack, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
