package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HeldItemRenderer implements FeatureRenderer<LivingEntity> {
	private final LivingEntityRenderer<?> entityRenderer;

	public HeldItemRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.entityRenderer = livingEntityRenderer;
	}

	@Override
	public void render(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale) {
		ItemStack itemStack = entity.getStackInHand();
		if (itemStack != null) {
			GlStateManager.pushMatrix();
			if (this.entityRenderer.getModel().child) {
				float f = 0.5F;
				GlStateManager.translate(0.0F, 0.625F, 0.0F);
				GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
				GlStateManager.scale(f, f, f);
			}

			((BiPedModel)this.entityRenderer.getModel()).setArmAngle(0.0625F);
			GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);
			if (entity instanceof PlayerEntity && ((PlayerEntity)entity).fishHook != null) {
				itemStack = new ItemStack(Items.FISHING_ROD, 0);
			}

			Item item = itemStack.getItem();
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			if (item instanceof BlockItem && Block.getBlockFromItem(item).getBlockType() == 2) {
				GlStateManager.translate(0.0F, 0.1875F, -0.3125F);
				GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
				float g = 0.375F;
				GlStateManager.scale(-g, -g, g);
			}

			if (entity.isSneaking()) {
				GlStateManager.translate(0.0F, 0.203125F, 0.0F);
			}

			minecraftClient.getHeldItemRenderer().renderItem(entity, itemStack, ModelTransformation.Mode.THIRD_PERSON);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
