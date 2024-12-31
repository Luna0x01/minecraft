package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.ModelBox;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;

public class StuckArrowsFeatureRenderer implements FeatureRenderer<LivingEntity> {
	private final LivingEntityRenderer<?> entityRenderer;

	public StuckArrowsFeatureRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.entityRenderer = livingEntityRenderer;
	}

	@Override
	public void render(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale) {
		int i = entity.getStuckArrows();
		if (i > 0) {
			Entity entity2 = new ArrowEntity(entity.world, entity.x, entity.y, entity.z);
			Random random = new Random((long)entity.getEntityId());
			DiffuseLighting.disable();

			for (int j = 0; j < i; j++) {
				GlStateManager.pushMatrix();
				ModelPart modelPart = this.entityRenderer.getModel().method_4273(random);
				ModelBox modelBox = (ModelBox)modelPart.cuboids.get(random.nextInt(modelPart.cuboids.size()));
				modelPart.preRender(0.0625F);
				float f = random.nextFloat();
				float g = random.nextFloat();
				float h = random.nextFloat();
				float k = (modelBox.minX + (modelBox.maxX - modelBox.minX) * f) / 16.0F;
				float l = (modelBox.minY + (modelBox.maxY - modelBox.minY) * g) / 16.0F;
				float m = (modelBox.minZ + (modelBox.maxZ - modelBox.minZ) * h) / 16.0F;
				GlStateManager.translate(k, l, m);
				f = f * 2.0F - 1.0F;
				g = g * 2.0F - 1.0F;
				h = h * 2.0F - 1.0F;
				f *= -1.0F;
				g *= -1.0F;
				h *= -1.0F;
				float n = MathHelper.sqrt(f * f + h * h);
				entity2.yaw = (float)(Math.atan2((double)f, (double)h) * 180.0F / (float)Math.PI);
				entity2.pitch = (float)(Math.atan2((double)g, (double)n) * 180.0F / (float)Math.PI);
				entity2.prevYaw = entity2.yaw;
				entity2.prevPitch = entity2.pitch;
				double d = 0.0;
				double e = 0.0;
				double o = 0.0;
				this.entityRenderer.getRenderManager().method_12446(entity2, 0.0, 0.0, 0.0, 0.0F, tickDelta, false);
				GlStateManager.popMatrix();
			}

			DiffuseLighting.enableNormally();
		}
	}

	@Override
	public boolean combineTextures() {
		return false;
	}
}
