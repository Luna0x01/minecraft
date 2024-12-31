package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4057;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public enum class_80 implements class_4057 {
	INSTANCE;

	private static final int field_19674 = Registry.BIOME.getRawId(Biomes.BEACH);
	private static final int field_19675 = Registry.BIOME.getRawId(Biomes.COLD_BEACH);
	private static final int field_19676 = Registry.BIOME.getRawId(Biomes.DESERT);
	private static final int field_19677 = Registry.BIOME.getRawId(Biomes.EXTREME_HILLS);
	private static final int field_19678 = Registry.BIOME.getRawId(Biomes.EXTREME_HILLS_WITH_TREES);
	private static final int field_19679 = Registry.BIOME.getRawId(Biomes.FOREST);
	private static final int field_19680 = Registry.BIOME.getRawId(Biomes.JUNGLE);
	private static final int field_19681 = Registry.BIOME.getRawId(Biomes.JUNGLE_EDGE);
	private static final int field_19682 = Registry.BIOME.getRawId(Biomes.JUNGLE_HILLS);
	private static final int field_19683 = Registry.BIOME.getRawId(Biomes.MESA);
	private static final int field_19684 = Registry.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
	private static final int field_19685 = Registry.BIOME.getRawId(Biomes.BADLANDS_PLATEAU);
	private static final int field_19686 = Registry.BIOME.getRawId(Biomes.MESA_M);
	private static final int field_19687 = Registry.BIOME.getRawId(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU);
	private static final int field_19688 = Registry.BIOME.getRawId(Biomes.MODIFIED_BADLANDS_PLATEAU);
	private static final int field_19689 = Registry.BIOME.getRawId(Biomes.MUSHROOM_ISLAND);
	private static final int field_19690 = Registry.BIOME.getRawId(Biomes.MUSHROOM_ISLAND_SHORE);
	private static final int field_19691 = Registry.BIOME.getRawId(Biomes.RIVER);
	private static final int field_19692 = Registry.BIOME.getRawId(Biomes.ExTREME_HILLS_SMALLER);
	private static final int field_19693 = Registry.BIOME.getRawId(Biomes.STONE_BEACH);
	private static final int field_19694 = Registry.BIOME.getRawId(Biomes.SWAMP);
	private static final int field_19695 = Registry.BIOME.getRawId(Biomes.TAIGA);

	@Override
	public int method_17892(class_4040 arg, int i, int j, int k, int l, int m) {
		Biome biome = Registry.BIOME.getByRawId(m);
		if (m == field_19689) {
			if (class_4046.method_17863(i) || class_4046.method_17863(j) || class_4046.method_17863(k) || class_4046.method_17863(l)) {
				return field_19690;
			}
		} else if (biome != null && biome.getCategory() == Biome.Category.JUNGLE) {
			if (!method_6603(i) || !method_6603(j) || !method_6603(k) || !method_6603(l)) {
				return field_19681;
			}

			if (class_4046.method_17857(i) || class_4046.method_17857(j) || class_4046.method_17857(k) || class_4046.method_17857(l)) {
				return field_19674;
			}
		} else if (m != field_19677 && m != field_19678 && m != field_19692) {
			if (biome != null && biome.getPrecipitation() == Biome.Precipitation.SNOW) {
				if (!class_4046.method_17857(m) && (class_4046.method_17857(i) || class_4046.method_17857(j) || class_4046.method_17857(k) || class_4046.method_17857(l))) {
					return field_19675;
				}
			} else if (m != field_19683 && m != field_19684) {
				if (!class_4046.method_17857(m)
					&& m != field_19691
					&& m != field_19694
					&& (class_4046.method_17857(i) || class_4046.method_17857(j) || class_4046.method_17857(k) || class_4046.method_17857(l))) {
					return field_19674;
				}
			} else if (!class_4046.method_17857(i)
				&& !class_4046.method_17857(j)
				&& !class_4046.method_17857(k)
				&& !class_4046.method_17857(l)
				&& (!this.method_6604(i) || !this.method_6604(j) || !this.method_6604(k) || !this.method_6604(l))) {
				return field_19676;
			}
		} else if (!class_4046.method_17857(m)
			&& (class_4046.method_17857(i) || class_4046.method_17857(j) || class_4046.method_17857(k) || class_4046.method_17857(l))) {
			return field_19693;
		}

		return m;
	}

	private static boolean method_6603(int i) {
		return Registry.BIOME.getByRawId(i) != null && Registry.BIOME.getByRawId(i).getCategory() == Biome.Category.JUNGLE
			? true
			: i == field_19681 || i == field_19680 || i == field_19682 || i == field_19679 || i == field_19695 || class_4046.method_17857(i);
	}

	private boolean method_6604(int i) {
		return i == field_19683 || i == field_19684 || i == field_19685 || i == field_19686 || i == field_19687 || i == field_19688;
	}
}
