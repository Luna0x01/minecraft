package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class class_4272 implements FeatureRenderer<AbstractClientPlayerEntity> {
	public static final Identifier field_20967 = new Identifier("textures/entity/trident_riptide.png");
	private final PlayerEntityRenderer field_20968;
	private final class_4272.class_4273 field_20969;

	public class_4272(PlayerEntityRenderer playerEntityRenderer) {
		this.field_20968 = playerEntityRenderer;
		this.field_20969 = new class_4272.class_4273();
	}

	public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (abstractClientPlayerEntity.method_15646()) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_20968.bindTexture(field_20967);

			for (int m = 0; m < 3; m++) {
				GlStateManager.pushMatrix();
				GlStateManager.rotate(i * (float)(-(45 + m * 5)), 0.0F, 1.0F, 0.0F);
				float n = 0.75F * (float)m;
				GlStateManager.scale(n, n, n);
				GlStateManager.translate(0.0F, -0.2F + 0.6F * (float)m, 0.0F);
				this.field_20969.render(abstractClientPlayerEntity, f, g, i, j, k, l);
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}

	static class class_4273 extends EntityModel {
		private final ModelPart field_20970;

		public class_4273() {
			this.textureWidth = 64;
			this.textureHeight = 64;
			this.field_20970 = new ModelPart(this, 0, 0);
			this.field_20970.addCuboid(-8.0F, -16.0F, -8.0F, 16, 32, 16);
		}

		@Override
		public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
			this.field_20970.render(scale);
		}
	}
}
