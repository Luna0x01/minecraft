package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.ElytraModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ElytraFeatureRenderer implements FeatureRenderer<AbstractClientPlayerEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/elytra.png");
	private final PlayerEntityRenderer field_13648;
	private final ElytraModel model = new ElytraModel();

	public ElytraFeatureRenderer(PlayerEntityRenderer playerEntityRenderer) {
		this.field_13648 = playerEntityRenderer;
	}

	public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = abstractClientPlayerEntity.getStack(EquipmentSlot.CHEST);
		if (itemStack != null && itemStack.getItem() == Items.ELYTRA) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableBlend();
			if (abstractClientPlayerEntity.method_12263() && abstractClientPlayerEntity.method_12264() != null) {
				this.field_13648.bindTexture(abstractClientPlayerEntity.method_12264());
			} else if (abstractClientPlayerEntity.canRenderCapeTexture()
				&& abstractClientPlayerEntity.getSkinId() != null
				&& abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE)) {
				this.field_13648.bindTexture(abstractClientPlayerEntity.getSkinId());
			} else {
				this.field_13648.bindTexture(TEXTURE);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, 0.125F);
			this.model.setAngles(f, g, i, j, k, l, abstractClientPlayerEntity);
			this.model.render(abstractClientPlayerEntity, f, g, i, j, k, l);
			if (itemStack.hasEnchantments()) {
				ArmorFeatureRenderer.method_12479(this.field_13648, abstractClientPlayerEntity, this.model, f, g, h, i, j, k, l);
			}

			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
