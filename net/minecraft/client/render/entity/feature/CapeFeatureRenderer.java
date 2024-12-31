package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

public class CapeFeatureRenderer implements FeatureRenderer<AbstractClientPlayerEntity> {
	private final PlayerEntityRenderer playerRenderer;

	public CapeFeatureRenderer(PlayerEntityRenderer playerEntityRenderer) {
		this.playerRenderer = playerEntityRenderer;
	}

	public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (abstractClientPlayerEntity.canRenderCapeTexture()
			&& !abstractClientPlayerEntity.isInvisible()
			&& abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE)
			&& abstractClientPlayerEntity.getSkinId() != null) {
			ItemStack itemStack = abstractClientPlayerEntity.getStack(EquipmentSlot.CHEST);
			if (itemStack.getItem() != Items.ELYTRA) {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.playerRenderer.bindTexture(abstractClientPlayerEntity.getSkinId());
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 0.0F, 0.125F);
				double d = abstractClientPlayerEntity.capeX
					+ (abstractClientPlayerEntity.prevCapeX - abstractClientPlayerEntity.capeX) * (double)h
					- (abstractClientPlayerEntity.prevX + (abstractClientPlayerEntity.x - abstractClientPlayerEntity.prevX) * (double)h);
				double e = abstractClientPlayerEntity.capeY
					+ (abstractClientPlayerEntity.prevCapeY - abstractClientPlayerEntity.capeY) * (double)h
					- (abstractClientPlayerEntity.prevY + (abstractClientPlayerEntity.y - abstractClientPlayerEntity.prevY) * (double)h);
				double m = abstractClientPlayerEntity.capeZ
					+ (abstractClientPlayerEntity.prevCapeZ - abstractClientPlayerEntity.capeZ) * (double)h
					- (abstractClientPlayerEntity.prevZ + (abstractClientPlayerEntity.z - abstractClientPlayerEntity.prevZ) * (double)h);
				float n = abstractClientPlayerEntity.prevBodyYaw + (abstractClientPlayerEntity.bodyYaw - abstractClientPlayerEntity.prevBodyYaw);
				double o = (double)MathHelper.sin(n * (float) (Math.PI / 180.0));
				double p = (double)(-MathHelper.cos(n * (float) (Math.PI / 180.0)));
				float q = (float)e * 10.0F;
				q = MathHelper.clamp(q, -6.0F, 32.0F);
				float r = (float)(d * o + m * p) * 100.0F;
				r = MathHelper.clamp(r, 0.0F, 150.0F);
				float s = (float)(d * p - m * o) * 100.0F;
				s = MathHelper.clamp(s, -20.0F, 20.0F);
				if (r < 0.0F) {
					r = 0.0F;
				}

				float t = abstractClientPlayerEntity.prevStrideDistance + (abstractClientPlayerEntity.strideDistance - abstractClientPlayerEntity.prevStrideDistance) * h;
				q += MathHelper.sin(
						(abstractClientPlayerEntity.prevHorizontalSpeed + (abstractClientPlayerEntity.horizontalSpeed - abstractClientPlayerEntity.prevHorizontalSpeed) * h)
							* 6.0F
					)
					* 32.0F
					* t;
				if (abstractClientPlayerEntity.isSneaking()) {
					q += 25.0F;
				}

				GlStateManager.rotate(6.0F + r / 2.0F + q, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(s / 2.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotate(-s / 2.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				this.playerRenderer.getModel().renderCape(0.0625F);
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
