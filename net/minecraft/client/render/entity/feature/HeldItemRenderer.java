package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class HeldItemRenderer implements FeatureRenderer<LivingEntity> {
	protected final LivingEntityRenderer<?> entityRenderer;

	public HeldItemRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.entityRenderer = livingEntityRenderer;
	}

	@Override
	public void render(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale) {
		boolean bl = entity.getDurability() == HandOption.RIGHT;
		ItemStack itemStack = bl ? entity.getOffHandStack() : entity.getMainHandStack();
		ItemStack itemStack2 = bl ? entity.getMainHandStack() : entity.getOffHandStack();
		if (itemStack != null || itemStack2 != null) {
			GlStateManager.pushMatrix();
			if (this.entityRenderer.getModel().child) {
				float f = 0.5F;
				GlStateManager.translate(0.0F, 0.625F, 0.0F);
				GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
			}

			this.method_12484(entity, itemStack2, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, HandOption.RIGHT);
			this.method_12484(entity, itemStack, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, HandOption.LEFT);
			GlStateManager.popMatrix();
		}
	}

	private void method_12484(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Mode mode, HandOption handOption) {
		if (itemStack != null) {
			GlStateManager.pushMatrix();
			((BiPedModel)this.entityRenderer.getModel()).method_12221(0.0625F, handOption);
			if (livingEntity.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}

			GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			boolean bl = handOption == HandOption.LEFT;
			GlStateManager.translate((float)(bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);
			MinecraftClient.getInstance().getHeldItemRenderer().method_12333(livingEntity, itemStack, mode, bl);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
