package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class DefaultFeatureConfig implements FeatureConfig {
	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic(dynamicOps, dynamicOps.emptyMap());
	}

	public static <T> DefaultFeatureConfig deserialize(Dynamic<T> dynamic) {
		return DEFAULT;
	}
}
