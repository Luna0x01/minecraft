package net.minecraft.world.biome.layer.util;

public interface IdentityCoordinateTransformer extends CoordinateTransformer {
	@Override
	default int transformX(int i) {
		return i;
	}

	@Override
	default int transformZ(int i) {
		return i;
	}
}
