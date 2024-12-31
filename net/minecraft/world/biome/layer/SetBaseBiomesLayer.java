package net.minecraft.world.biome.layer;

import net.minecraft.class_3809;
import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4055;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.level.LevelGeneratorType;

public class SetBaseBiomesLayer implements class_4055 {
	private static final int field_19582 = Registry.BIOME.getRawId(Biomes.BIRCH_FOREST);
	private static final int field_19583 = Registry.BIOME.getRawId(Biomes.DESERT);
	private static final int field_19584 = Registry.BIOME.getRawId(Biomes.EXTREME_HILLS);
	private static final int field_19585 = Registry.BIOME.getRawId(Biomes.FOREST);
	private static final int field_19586 = Registry.BIOME.getRawId(Biomes.ICE_FLATS);
	private static final int field_19587 = Registry.BIOME.getRawId(Biomes.JUNGLE);
	private static final int field_19588 = Registry.BIOME.getRawId(Biomes.BADLANDS_PLATEAU);
	private static final int field_19589 = Registry.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
	private static final int field_19590 = Registry.BIOME.getRawId(Biomes.MUSHROOM_ISLAND);
	private static final int field_19591 = Registry.BIOME.getRawId(Biomes.PLAINS);
	private static final int field_19592 = Registry.BIOME.getRawId(Biomes.GIANT_TREE_TAIGA);
	private static final int field_19593 = Registry.BIOME.getRawId(Biomes.ROOFED_FOREST);
	private static final int field_19594 = Registry.BIOME.getRawId(Biomes.SAVANNA);
	private static final int field_19595 = Registry.BIOME.getRawId(Biomes.SWAMP);
	private static final int field_19596 = Registry.BIOME.getRawId(Biomes.TAIGA);
	private static final int field_19597 = Registry.BIOME.getRawId(Biomes.TAIGA_COLD);
	private static final int[] field_19598 = new int[]{field_19583, field_19585, field_19584, field_19595, field_19591, field_19596};
	private static final int[] field_19599 = new int[]{field_19583, field_19583, field_19583, field_19594, field_19594, field_19591};
	private static final int[] field_19600 = new int[]{field_19585, field_19593, field_19584, field_19591, field_19582, field_19595};
	private static final int[] field_19601 = new int[]{field_19585, field_19584, field_19596, field_19591};
	private static final int[] field_19602 = new int[]{field_19586, field_19586, field_19586, field_19597};
	private final class_3809 field_10232;
	private int[] field_19603 = field_19599;

	public SetBaseBiomesLayer(LevelGeneratorType levelGeneratorType, class_3809 arg) {
		if (levelGeneratorType == LevelGeneratorType.DEFAULT_1_1) {
			this.field_19603 = field_19598;
			this.field_10232 = null;
		} else {
			this.field_10232 = arg;
		}
	}

	@Override
	public int method_17890(class_4040 arg, int i) {
		if (this.field_10232 != null && this.field_10232.method_17270() >= 0) {
			return this.field_10232.method_17270();
		} else {
			int j = (i & 3840) >> 8;
			i &= -3841;
			if (!class_4046.method_17857(i) && i != field_19590) {
				switch (i) {
					case 1:
						if (j > 0) {
							return arg.method_17850(3) == 0 ? field_19588 : field_19589;
						}

						return this.field_19603[arg.method_17850(this.field_19603.length)];
					case 2:
						if (j > 0) {
							return field_19587;
						}

						return field_19600[arg.method_17850(field_19600.length)];
					case 3:
						if (j > 0) {
							return field_19592;
						}

						return field_19601[arg.method_17850(field_19601.length)];
					case 4:
						return field_19602[arg.method_17850(field_19602.length)];
					default:
						return field_19590;
				}
			} else {
				return i;
			}
		}
	}
}
