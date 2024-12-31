package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class AreaEffectCloudEntityRenderer extends EntityRenderer<Entity> {
	public AreaEffectCloudEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	@Override
	public void render(Entity entity, double x, double y, double z, float yaw, float tickDelta) {
		GlStateManager.pushMatrix();
		method_1527(entity.getBoundingBox(), x - entity.prevTickX, y - entity.prevTickY, z - entity.prevTickZ);
		GlStateManager.popMatrix();
		super.render(entity, x, y, z, yaw, tickDelta);
	}

	@Override
	protected Identifier getTexture(Entity entity) {
		return null;
	}
}
