package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4057;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum AddRiverLayer implements class_4057 {
	INSTANCE;

	public static final int field_19664 = Registry.BIOME.getRawId(Biomes.RIVER);

	@Override
	public int method_17892(class_4040 arg, int i, int j, int k, int l, int m) {
		int n = method_6601(m);
		return n == method_6601(l) && n == method_6601(i) && n == method_6601(j) && n == method_6601(k) ? -1 : field_19664;
	}

	private static int method_6601(int i) {
		return i >= 2 ? 2 + (i & 1) : i;
	}
}
