package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

public class Deadmau5FeatureRenderer implements FeatureRenderer<AbstractClientPlayerEntity> {
	private final PlayerEntityRenderer playerRenderer;

	public Deadmau5FeatureRenderer(PlayerEntityRenderer playerEntityRenderer) {
		this.playerRenderer = playerEntityRenderer;
	}

	public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (abstractClientPlayerEntity.getTranslationKey().equals("deadmau5")
			&& abstractClientPlayerEntity.hasSkinTexture()
			&& !abstractClientPlayerEntity.isInvisible()) {
			this.playerRenderer.bindTexture(abstractClientPlayerEntity.getCapeId());

			for (int m = 0; m < 2; m++) {
				float n = abstractClientPlayerEntity.prevYaw
					+ (abstractClientPlayerEntity.yaw - abstractClientPlayerEntity.prevYaw) * h
					- (abstractClientPlayerEntity.prevBodyYaw + (abstractClientPlayerEntity.bodyYaw - abstractClientPlayerEntity.prevBodyYaw) * h);
				float o = abstractClientPlayerEntity.prevPitch + (abstractClientPlayerEntity.pitch - abstractClientPlayerEntity.prevPitch) * h;
				GlStateManager.pushMatrix();
				GlStateManager.rotate(n, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(o, 1.0F, 0.0F, 0.0F);
				GlStateManager.translate(0.375F * (float)(m * 2 - 1), 0.0F, 0.0F);
				GlStateManager.translate(0.0F, -0.375F, 0.0F);
				GlStateManager.rotate(-o, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-n, 0.0F, 1.0F, 0.0F);
				float p = 1.3333334F;
				GlStateManager.scale(p, p, p);
				this.playerRenderer.getModel().renderEars(0.0625F);
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean combineTextures() {
		return true;
	}
}
