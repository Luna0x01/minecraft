package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.ElytraModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ElytraFeatureRenderer implements FeatureRenderer<LivingEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/elytra.png");
	protected final LivingEntityRenderer<?> field_15316;
	private final ElytraModel model = new ElytraModel();

	public ElytraFeatureRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.field_15316 = livingEntityRenderer;
	}

	@Override
	public void render(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale) {
		ItemStack itemStack = entity.getStack(EquipmentSlot.CHEST);
		if (itemStack.getItem() == Items.ELYTRA) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableBlend();
			GlStateManager.method_12287(GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO);
			if (entity instanceof AbstractClientPlayerEntity) {
				AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity;
				if (abstractClientPlayerEntity.method_12263() && abstractClientPlayerEntity.method_12264() != null) {
					this.field_15316.bindTexture(abstractClientPlayerEntity.method_12264());
				} else if (abstractClientPlayerEntity.canRenderCapeTexture()
					&& abstractClientPlayerEntity.getSkinId() != null
					&& abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE)) {
					this.field_15316.bindTexture(abstractClientPlayerEntity.getSkinId());
				} else {
					this.field_15316.bindTexture(TEXTURE);
				}
			} else {
				this.field_15316.bindTexture(TEXTURE);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, 0.125F);
			this.model.setAngles(handSwing, handSwingAmount, age, headYaw, headPitch, scale, entity);
			this.model.render(entity, handSwing, handSwingAmount, age, headYaw, headPitch, scale);
			if (itemStack.hasEnchantments()) {
				ArmorFeatureRenderer.method_12479(this.field_15316, entity, this.model, handSwing, handSwingAmount, tickDelta, age, headYaw, headPitch, scale);
			}

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
