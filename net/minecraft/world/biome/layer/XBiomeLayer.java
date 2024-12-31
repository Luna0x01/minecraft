package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4057;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public enum XBiomeLayer implements class_4057 {
	INSTANCE;

	private static final int field_19566 = Registry.BIOME.getRawId(Biomes.DESERT);
	private static final int field_19567 = Registry.BIOME.getRawId(Biomes.EXTREME_HILLS);
	private static final int field_19568 = Registry.BIOME.getRawId(Biomes.EXTREME_HILLS_WITH_TREES);
	private static final int field_19569 = Registry.BIOME.getRawId(Biomes.ICE_FLATS);
	private static final int field_19570 = Registry.BIOME.getRawId(Biomes.JUNGLE);
	private static final int field_19571 = Registry.BIOME.getRawId(Biomes.JUNGLE_EDGE);
	private static final int field_19572 = Registry.BIOME.getRawId(Biomes.MESA);
	private static final int field_19573 = Registry.BIOME.getRawId(Biomes.BADLANDS_PLATEAU);
	private static final int field_19574 = Registry.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
	private static final int field_19575 = Registry.BIOME.getRawId(Biomes.PLAINS);
	private static final int field_19576 = Registry.BIOME.getRawId(Biomes.GIANT_TREE_TAIGA);
	private static final int field_19577 = Registry.BIOME.getRawId(Biomes.ExTREME_HILLS_SMALLER);
	private static final int field_19578 = Registry.BIOME.getRawId(Biomes.SWAMP);
	private static final int field_19579 = Registry.BIOME.getRawId(Biomes.TAIGA);
	private static final int field_19580 = Registry.BIOME.getRawId(Biomes.TAIGA_COLD);

	@Override
	public int method_17892(class_4040 arg, int i, int j, int k, int l, int m) {
		int[] is = new int[1];
		if (!this.method_6592(is, i, j, k, l, m, field_19567, field_19577)
			&& !this.method_17855(is, i, j, k, l, m, field_19574, field_19572)
			&& !this.method_17855(is, i, j, k, l, m, field_19573, field_19572)
			&& !this.method_17855(is, i, j, k, l, m, field_19576, field_19579)) {
			if (m != field_19566 || i != field_19569 && j != field_19569 && l != field_19569 && k != field_19569) {
				if (m == field_19578) {
					if (i == field_19566
						|| j == field_19566
						|| l == field_19566
						|| k == field_19566
						|| i == field_19580
						|| j == field_19580
						|| l == field_19580
						|| k == field_19580
						|| i == field_19569
						|| j == field_19569
						|| l == field_19569
						|| k == field_19569) {
						return field_19575;
					}

					if (i == field_19570 || k == field_19570 || j == field_19570 || l == field_19570) {
						return field_19571;
					}
				}

				return m;
			} else {
				return field_19568;
			}
		} else {
			return is[0];
		}
	}

	private boolean method_6592(int[] is, int i, int j, int k, int l, int m, int n, int o) {
		if (!class_4046.method_17858(m, n)) {
			return false;
		} else {
			if (this.method_6593(i, n) && this.method_6593(j, n) && this.method_6593(l, n) && this.method_6593(k, n)) {
				is[0] = m;
			} else {
				is[0] = o;
			}

			return true;
		}
	}

	private boolean method_17855(int[] is, int i, int j, int k, int l, int m, int n, int o) {
		if (m != n) {
			return false;
		} else {
			if (class_4046.method_17858(i, n) && class_4046.method_17858(j, n) && class_4046.method_17858(l, n) && class_4046.method_17858(k, n)) {
				is[0] = m;
			} else {
				is[0] = o;
			}

			return true;
		}
	}

	private boolean method_6593(int i, int j) {
		if (class_4046.method_17858(i, j)) {
			return true;
		} else {
			Biome biome = Registry.BIOME.getByRawId(i);
			Biome biome2 = Registry.BIOME.getByRawId(j);
			if (biome != null && biome2 != null) {
				Biome.Temperature temperature = biome.getBiomeTemperature();
				Biome.Temperature temperature2 = biome2.getBiomeTemperature();
				return temperature == temperature2 || temperature == Biome.Temperature.MEDIUM || temperature2 == Biome.Temperature.MEDIUM;
			} else {
				return false;
			}
		}
	}
}
