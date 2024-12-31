package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class class_4268 implements FeatureRenderer<LivingEntity> {
	protected final LivingEntityRenderer<?> field_20955;
	private final HeldItemRenderer field_20956;

	public class_4268(LivingEntityRenderer<?> livingEntityRenderer) {
		this.field_20955 = livingEntityRenderer;
		this.field_20956 = MinecraftClient.getInstance().getHeldItemRenderer();
	}

	@Override
	public void render(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale) {
		boolean bl = entity.getDurability() == HandOption.RIGHT;
		ItemStack itemStack = bl ? entity.getOffHandStack() : entity.getMainHandStack();
		ItemStack itemStack2 = bl ? entity.getMainHandStack() : entity.getOffHandStack();
		if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
			this.method_19432(entity, itemStack2);
		}
	}

	private void method_19432(LivingEntity livingEntity, ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			if (!itemStack.isEmpty()) {
				Item item = itemStack.getItem();
				Block block = Block.getBlockFromItem(item);
				GlStateManager.pushMatrix();
				boolean bl = this.field_20956.method_19375(itemStack) && block.getRenderLayerType() == RenderLayer.TRANSLUCENT;
				if (bl) {
					GlStateManager.depthMask(false);
				}

				float f = 1.0F;
				float g = -1.0F;
				float h = MathHelper.abs(livingEntity.pitch) / 60.0F;
				if (livingEntity.pitch < 0.0F) {
					GlStateManager.translate(0.0F, 1.0F - h * 0.5F, -1.0F + h * 0.5F);
				} else {
					GlStateManager.translate(0.0F, 1.0F + h * 0.8F, -1.0F + h * 0.2F);
				}

				this.field_20956.method_19378(itemStack, livingEntity, ModelTransformation.Mode.GROUND, false);
				if (bl) {
					GlStateManager.depthMask(true);
				}

				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
