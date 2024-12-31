package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.WitchEntityRenderer;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.item.BlockItem;
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
		if (itemStack != null) {
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			if (this.witchRenderer.getModel().child) {
				GlStateManager.translate(0.0F, 0.625F, 0.0F);
				GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
				float m = 0.5F;
				GlStateManager.scale(m, m, m);
			}

			((WitchEntityModel)this.witchRenderer.getModel()).field_5132.preRender(0.0625F);
			GlStateManager.translate(-0.0625F, 0.53125F, 0.21875F);
			Item item = itemStack.getItem();
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			if (item instanceof BlockItem && minecraftClient.getBlockRenderManager().method_12345(Block.getBlockFromItem(item))) {
				GlStateManager.translate(0.0F, 0.0625F, -0.25F);
				GlStateManager.rotate(30.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-5.0F, 0.0F, 1.0F, 0.0F);
				float n = 0.375F;
				GlStateManager.scale(n, -n, n);
			} else if (item == Items.BOW) {
				GlStateManager.translate(0.0F, 0.125F, -0.125F);
				GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
				float o = 0.625F;
				GlStateManager.scale(o, -o, o);
				GlStateManager.rotate(-100.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-20.0F, 0.0F, 1.0F, 0.0F);
			} else if (item.isHandheld()) {
				if (item.shouldRotate()) {
					GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.translate(0.0F, -0.0625F, 0.0F);
				}

				this.witchRenderer.translate();
				GlStateManager.translate(0.0625F, -0.125F, 0.0F);
				float p = 0.625F;
				GlStateManager.scale(p, -p, p);
				GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
			} else {
				GlStateManager.translate(0.1875F, 0.1875F, 0.0F);
				float q = 0.875F;
				GlStateManager.scale(q, q, q);
				GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotate(-60.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-30.0F, 0.0F, 0.0F, 1.0F);
			}

			GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(40.0F, 0.0F, 0.0F, 1.0F);
			minecraftClient.getHeldItemRenderer().renderItem(witchEntity, itemStack, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
