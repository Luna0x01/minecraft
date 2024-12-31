package net.minecraft.client.render.model;

import net.minecraft.client.util.math.Rotation3;

public interface ModelBakeSettings {
	default Rotation3 getRotation() {
		return Rotation3.identity();
	}

	default boolean isShaded() {
		return false;
	}
}
