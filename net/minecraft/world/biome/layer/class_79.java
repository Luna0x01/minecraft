package net.minecraft.world.biome.layer;

import net.minecraft.class_4035;
import net.minecraft.class_4036;
import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4053;
import net.minecraft.class_4058;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum class_79 implements class_4053, class_4058 {
	INSTANCE;

	private static final int field_19667 = Registry.BIOME.getRawId(Biomes.FROZEN_RIVER);
	private static final int field_19668 = Registry.BIOME.getRawId(Biomes.ICE_FLATS);
	private static final int field_19669 = Registry.BIOME.getRawId(Biomes.MUSHROOM_ISLAND);
	private static final int field_19670 = Registry.BIOME.getRawId(Biomes.MUSHROOM_ISLAND_SHORE);
	private static final int field_19671 = Registry.BIOME.getRawId(Biomes.RIVER);

	@Override
	public int method_17888(class_4040 arg, class_4036 arg2, class_4035 arg3, class_4035 arg4, int i, int j) {
		int k = arg3.method_17837(i, j);
		int l = arg4.method_17837(i, j);
		if (class_4046.method_17857(k)) {
			return k;
		} else if (l == field_19671) {
			if (k == field_19668) {
				return field_19667;
			} else {
				return k != field_19669 && k != field_19670 ? l & 0xFF : field_19670;
			}
		} else {
			return k;
		}
	}
}
