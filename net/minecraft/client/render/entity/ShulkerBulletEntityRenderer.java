package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.entity.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ShulkerBulletEntityRenderer extends EntityRenderer<ShulkerBulletEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/shulker/spark.png");
	private final ShulkerBulletEntityModel model = new ShulkerBulletEntityModel();

	public ShulkerBulletEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	private float method_12466(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}

	public void render(ShulkerBulletEntity shulkerBulletEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		float i = this.method_12466(shulkerBulletEntity.prevYaw, shulkerBulletEntity.yaw, h);
		float j = shulkerBulletEntity.prevPitch + (shulkerBulletEntity.pitch - shulkerBulletEntity.prevPitch) * h;
		float k = (float)shulkerBulletEntity.ticksAlive + h;
		GlStateManager.translate((float)d, (float)e + 0.15F, (float)f);
		GlStateManager.rotate(MathHelper.sin(k * 0.1F) * 180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(MathHelper.cos(k * 0.1F) * 180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(MathHelper.sin(k * 0.15F) * 360.0F, 0.0F, 0.0F, 1.0F);
		float l = 0.03125F;
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		this.bindTexture(shulkerBulletEntity);
		this.model.render(shulkerBulletEntity, 0.0F, 0.0F, 0.0F, i, j, 0.03125F);
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
		GlStateManager.scale(1.5F, 1.5F, 1.5F);
		this.model.render(shulkerBulletEntity, 0.0F, 0.0F, 0.0F, i, j, 0.03125F);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		super.render(shulkerBulletEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(ShulkerBulletEntity shulkerBulletEntity) {
		return TEXTURE;
	}
}
