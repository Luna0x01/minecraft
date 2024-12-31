package net.minecraft.client.render.model;

public interface ModelBakeSettings {
	default ModelRotation getRotation() {
		return ModelRotation.field_5350;
	}

	default boolean isUvLocked() {
		return false;
	}
}
