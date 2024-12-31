package net.minecraft.world.biome.layer;

import net.minecraft.class_4035;
import net.minecraft.class_4036;
import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4053;
import net.minecraft.class_4059;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum AddHillsLayer implements class_4053, class_4059 {
	INSTANCE;

	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_19635 = Registry.BIOME.getRawId(Biomes.BIRCH_FOREST);
	private static final int field_19636 = Registry.BIOME.getRawId(Biomes.BIRCH_FOREST_HILLS);
	private static final int field_19637 = Registry.BIOME.getRawId(Biomes.DESERT);
	private static final int field_19638 = Registry.BIOME.getRawId(Biomes.DESERT_HILLS);
	private static final int field_19639 = Registry.BIOME.getRawId(Biomes.EXTREME_HILLS);
	private static final int field_19640 = Registry.BIOME.getRawId(Biomes.EXTREME_HILLS_WITH_TREES);
	private static final int field_19641 = Registry.BIOME.getRawId(Biomes.FOREST);
	private static final int field_19642 = Registry.BIOME.getRawId(Biomes.FOREST_HILLS);
	private static final int field_19643 = Registry.BIOME.getRawId(Biomes.ICE_FLATS);
	private static final int field_19644 = Registry.BIOME.getRawId(Biomes.ICE_MOUNTAINS);
	private static final int field_19645 = Registry.BIOME.getRawId(Biomes.JUNGLE);
	private static final int field_19646 = Registry.BIOME.getRawId(Biomes.JUNGLE_HILLS);
	private static final int field_19647 = Registry.BIOME.getRawId(Biomes.MESA);
	private static final int field_19648 = Registry.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
	private static final int field_19649 = Registry.BIOME.getRawId(Biomes.PLAINS);
	private static final int field_19650 = Registry.BIOME.getRawId(Biomes.GIANT_TREE_TAIGA);
	private static final int field_19651 = Registry.BIOME.getRawId(Biomes.GIANT_TREE_TAIGA_HILLS);
	private static final int field_19652 = Registry.BIOME.getRawId(Biomes.ROOFED_FOREST);
	private static final int field_19653 = Registry.BIOME.getRawId(Biomes.SAVANNA);
	private static final int field_19654 = Registry.BIOME.getRawId(Biomes.SAVANNA_PLATEAU);
	private static final int field_19655 = Registry.BIOME.getRawId(Biomes.TAIGA);
	private static final int field_19656 = Registry.BIOME.getRawId(Biomes.TAIGA_COLD);
	private static final int field_19657 = Registry.BIOME.getRawId(Biomes.TAIGA_COLD_HILLS);
	private static final int field_19658 = Registry.BIOME.getRawId(Biomes.TAIGA_HILLS);

	@Override
	public int method_17888(class_4040 arg, class_4036 arg2, class_4035 arg3, class_4035 arg4, int i, int j) {
		int k = arg3.method_17837(i + 1, j + 1);
		int l = arg4.method_17837(i + 1, j + 1);
		if (k > 255) {
			LOGGER.debug("old! {}", k);
		}

		int m = (l - 2) % 29;
		if (!class_4046.method_17863(k) && l >= 2 && m == 1) {
			Biome biome = Registry.BIOME.getByRawId(k);
			if (biome == null || !biome.hasParent()) {
				Biome biome2 = Biome.getBiomeFromList(biome);
				return biome2 == null ? k : Registry.BIOME.getRawId(biome2);
			}
		}

		if (arg.method_17850(3) == 0 || m == 0) {
			int n = k;
			if (k == field_19637) {
				n = field_19638;
			} else if (k == field_19641) {
				n = field_19642;
			} else if (k == field_19635) {
				n = field_19636;
			} else if (k == field_19652) {
				n = field_19649;
			} else if (k == field_19655) {
				n = field_19658;
			} else if (k == field_19650) {
				n = field_19651;
			} else if (k == field_19656) {
				n = field_19657;
			} else if (k == field_19649) {
				n = arg.method_17850(3) == 0 ? field_19642 : field_19641;
			} else if (k == field_19643) {
				n = field_19644;
			} else if (k == field_19645) {
				n = field_19646;
			} else if (k == class_4046.field_19609) {
				n = class_4046.field_19614;
			} else if (k == class_4046.field_19608) {
				n = class_4046.field_19613;
			} else if (k == class_4046.field_19610) {
				n = class_4046.field_19615;
			} else if (k == class_4046.field_19611) {
				n = class_4046.field_19616;
			} else if (k == field_19639) {
				n = field_19640;
			} else if (k == field_19653) {
				n = field_19654;
			} else if (class_4046.method_17858(k, field_19648)) {
				n = field_19647;
			} else if ((k == class_4046.field_19614 || k == class_4046.field_19613 || k == class_4046.field_19615 || k == class_4046.field_19616)
				&& arg.method_17850(3) == 0) {
				n = arg.method_17850(2) == 0 ? field_19649 : field_19641;
			}

			if (m == 0 && n != k) {
				Biome biome3 = Biome.getBiomeFromList(Registry.BIOME.getByRawId(n));
				n = biome3 == null ? k : Registry.BIOME.getRawId(biome3);
			}

			if (n != k) {
				int o = 0;
				if (class_4046.method_17858(arg3.method_17837(i + 1, j + 0), k)) {
					o++;
				}

				if (class_4046.method_17858(arg3.method_17837(i + 2, j + 1), k)) {
					o++;
				}

				if (class_4046.method_17858(arg3.method_17837(i + 0, j + 1), k)) {
					o++;
				}

				if (class_4046.method_17858(arg3.method_17837(i + 1, j + 2), k)) {
					o++;
				}

				if (o >= 3) {
					return n;
				}
			}
		}

		return k;
	}
}
