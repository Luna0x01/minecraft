package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4056;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum class_82 implements class_4056 {
	INSTANCE;

	private static final int field_19630 = Registry.BIOME.getRawId(Biomes.PLAINS);
	private static final int field_19631 = Registry.BIOME.getRawId(Biomes.PLAINS_M);

	@Override
	public int method_17891(class_4040 arg, int i) {
		return arg.method_17850(57) == 0 && i == field_19630 ? field_19631 : i;
	}
}
