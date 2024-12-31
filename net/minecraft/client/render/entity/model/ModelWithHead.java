package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Cuboid;

public interface ModelWithHead {
	Cuboid getHead();

	default void setHeadAngle(float f) {
		this.getHead().applyTransform(f);
	}
}
