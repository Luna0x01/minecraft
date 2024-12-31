package net.minecraft.client.render.block.entity;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;

public abstract class SkullBlockEntityModel extends Model {
	public SkullBlockEntityModel() {
		super(RenderLayer::getEntityTranslucent);
	}

	public abstract void setHeadRotation(float animationProgress, float yaw, float pitch);
}
