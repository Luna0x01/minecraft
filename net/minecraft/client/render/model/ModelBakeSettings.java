package net.minecraft.client.render.model;

import net.minecraft.client.util.math.AffineTransformation;

public interface ModelBakeSettings {
	default AffineTransformation getRotation() {
		return AffineTransformation.identity();
	}

	default boolean isShaded() {
		return false;
	}
}
