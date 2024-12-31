package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4054;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum MushroomIslandLayer implements class_4054 {
	INSTANCE;

	private static final int field_19561 = Registry.BIOME.getRawId(Biomes.MUSHROOM_ISLAND);

	@Override
	public int method_17889(class_4040 arg, int i, int j, int k, int l, int m) {
		return class_4046.method_17863(m)
				&& class_4046.method_17863(l)
				&& class_4046.method_17863(i)
				&& class_4046.method_17863(k)
				&& class_4046.method_17863(j)
				&& arg.method_17850(100) == 0
			? field_19561
			: m;
	}
}
