package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.function.LongFunction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.AddHillsLayer;
import net.minecraft.world.biome.layer.AddRiverLayer;
import net.minecraft.world.biome.layer.EdgeLayer;
import net.minecraft.world.biome.layer.Layer;
import net.minecraft.world.biome.layer.MushroomIslandLayer;
import net.minecraft.world.biome.layer.SetBaseBiomesLayer;
import net.minecraft.world.biome.layer.XBiomeLayer;
import net.minecraft.world.biome.layer.class_1781;
import net.minecraft.world.biome.layer.class_1788;
import net.minecraft.world.biome.layer.class_67;
import net.minecraft.world.biome.layer.class_69;
import net.minecraft.world.biome.layer.class_74;
import net.minecraft.world.biome.layer.class_77;
import net.minecraft.world.biome.layer.class_79;
import net.minecraft.world.biome.layer.class_80;
import net.minecraft.world.biome.layer.class_81;
import net.minecraft.world.biome.layer.class_82;
import net.minecraft.world.biome.layer.class_83;
import net.minecraft.world.biome.layer.class_84;
import net.minecraft.world.level.LevelGeneratorType;

public class class_4046 {
	protected static final int field_19607 = Registry.BIOME.getRawId(Biomes.WARM_OCEAN);
	protected static final int field_19608 = Registry.BIOME.getRawId(Biomes.LUKEWARM_OCEAN);
	protected static final int field_19609 = Registry.BIOME.getRawId(Biomes.OCEAN);
	protected static final int field_19610 = Registry.BIOME.getRawId(Biomes.COLD_OCEAN);
	protected static final int field_19611 = Registry.BIOME.getRawId(Biomes.FROZEN_OCEAN);
	protected static final int field_19612 = Registry.BIOME.getRawId(Biomes.DEEP_WARM_OCEAN);
	protected static final int field_19613 = Registry.BIOME.getRawId(Biomes.DEEP_LUKEWARM_OCEAN);
	protected static final int field_19614 = Registry.BIOME.getRawId(Biomes.DEEP_OCEAN);
	protected static final int field_19615 = Registry.BIOME.getRawId(Biomes.DEEP_COLD_OCEAN);
	protected static final int field_19616 = Registry.BIOME.getRawId(Biomes.DEEP_FROZEN_OCEAN);

	private static <T extends class_4035, C extends class_4039<T>> class_4037<T> method_17860(
		long l, class_4052 arg, class_4037<T> arg2, int i, LongFunction<C> longFunction
	) {
		class_4037<T> lv = arg2;

		for (int j = 0; j < i; j++) {
			lv = arg.method_17883((class_4039<T>)longFunction.apply(l + (long)j), lv);
		}

		return lv;
	}

	public static <T extends class_4035, C extends class_4039<T>> ImmutableList<class_4037<T>> method_17861(
		LevelGeneratorType levelGeneratorType, class_3809 arg, LongFunction<C> longFunction
	) {
		class_4037<T> lv = class_74.INSTANCE.method_17877((class_4039<T>)longFunction.apply(1L));
		lv = class_84.FUZZY.method_17883((class_4039<T>)longFunction.apply(2000L), lv);
		lv = class_67.INSTANCE.method_17883((class_4039<T>)longFunction.apply(1L), lv);
		lv = class_84.NORMAL.method_17883((class_4039<T>)longFunction.apply(2001L), lv);
		lv = class_67.INSTANCE.method_17883((class_4039<T>)longFunction.apply(2L), lv);
		lv = class_67.INSTANCE.method_17883((class_4039<T>)longFunction.apply(50L), lv);
		lv = class_67.INSTANCE.method_17883((class_4039<T>)longFunction.apply(70L), lv);
		lv = class_1788.INSTANCE.method_17883((class_4039<T>)longFunction.apply(2L), lv);
		class_4037<T> lv2 = class_4047.INSTANCE.method_17877((class_4039<T>)longFunction.apply(2L));
		lv2 = method_17860(2001L, class_84.NORMAL, lv2, 6, longFunction);
		lv = class_69.INSTANCE.method_17883((class_4039<T>)longFunction.apply(2L), lv);
		lv = class_67.INSTANCE.method_17883((class_4039<T>)longFunction.apply(3L), lv);
		lv = EdgeLayer.class_4043.INSTANCE.method_17883((class_4039<T>)longFunction.apply(2L), lv);
		lv = EdgeLayer.class_4044.INSTANCE.method_17883((class_4039<T>)longFunction.apply(2L), lv);
		lv = EdgeLayer.class_4045.INSTANCE.method_17883((class_4039<T>)longFunction.apply(3L), lv);
		lv = class_84.NORMAL.method_17883((class_4039<T>)longFunction.apply(2002L), lv);
		lv = class_84.NORMAL.method_17883((class_4039<T>)longFunction.apply(2003L), lv);
		lv = class_67.INSTANCE.method_17883((class_4039<T>)longFunction.apply(4L), lv);
		lv = MushroomIslandLayer.INSTANCE.method_17883((class_4039<T>)longFunction.apply(5L), lv);
		lv = class_1781.INSTANCE.method_17883((class_4039<T>)longFunction.apply(4L), lv);
		lv = method_17860(1000L, class_84.NORMAL, lv, 0, longFunction);
		int i = 4;
		int j = i;
		if (arg != null) {
			i = arg.method_17268();
			j = arg.method_17269();
		}

		if (levelGeneratorType == LevelGeneratorType.LARGE_BIOMES) {
			i = 6;
		}

		class_4037<T> lv3 = method_17860(1000L, class_84.NORMAL, lv, 0, longFunction);
		lv3 = class_77.INSTANCE.method_17883((class_4039)longFunction.apply(100L), lv3);
		class_4037<T> lv4 = new SetBaseBiomesLayer(levelGeneratorType, arg).method_17883((class_4039<T>)longFunction.apply(200L), lv);
		lv4 = method_17860(1000L, class_84.NORMAL, lv4, 2, longFunction);
		lv4 = XBiomeLayer.INSTANCE.method_17883((class_4039)longFunction.apply(1000L), lv4);
		class_4037<T> lv5 = method_17860(1000L, class_84.NORMAL, lv3, 2, longFunction);
		lv4 = AddHillsLayer.INSTANCE.method_17887((class_4039)longFunction.apply(1000L), lv4, lv5);
		lv3 = method_17860(1000L, class_84.NORMAL, lv3, 2, longFunction);
		lv3 = method_17860(1000L, class_84.NORMAL, lv3, j, longFunction);
		lv3 = AddRiverLayer.INSTANCE.method_17883((class_4039)longFunction.apply(1L), lv3);
		lv3 = class_81.INSTANCE.method_17883((class_4039)longFunction.apply(1000L), lv3);
		lv4 = class_82.INSTANCE.method_17883((class_4039)longFunction.apply(1001L), lv4);

		for (int k = 0; k < i; k++) {
			lv4 = class_84.NORMAL.method_17883((class_4039)longFunction.apply((long)(1000 + k)), lv4);
			if (k == 0) {
				lv4 = class_67.INSTANCE.method_17883((class_4039)longFunction.apply(3L), lv4);
			}

			if (k == 1 || i == 1) {
				lv4 = class_80.INSTANCE.method_17883((class_4039)longFunction.apply(1000L), lv4);
			}
		}

		lv4 = class_81.INSTANCE.method_17883((class_4039)longFunction.apply(1000L), lv4);
		lv4 = class_79.INSTANCE.method_17887((class_4039)longFunction.apply(100L), lv4, lv3);
		lv4 = class_4050.INSTANCE.method_17887((class_4039<T>)longFunction.apply(100L), lv4, lv2);
		class_4037<T> lv7 = class_83.INSTANCE.method_17883((class_4039<T>)longFunction.apply(10L), lv4);
		return ImmutableList.of(lv4, lv7, lv4);
	}

	public static Layer[] method_17859(long l, LevelGeneratorType levelGeneratorType, class_3809 arg) {
		int i = 1;
		int[] is = new int[1];
		ImmutableList<class_4037<class_4038>> immutableList = method_17861(levelGeneratorType, arg, m -> {
			is[0]++;
			return new class_4042(1, is[0], l, m);
		});
		Layer layer = new Layer((class_4037<class_4038>)immutableList.get(0));
		Layer layer2 = new Layer((class_4037<class_4038>)immutableList.get(1));
		Layer layer3 = new Layer((class_4037<class_4038>)immutableList.get(2));
		return new Layer[]{layer, layer2, layer3};
	}

	public static boolean method_17858(int i, int j) {
		if (i == j) {
			return true;
		} else {
			Biome biome = Registry.BIOME.getByRawId(i);
			Biome biome2 = Registry.BIOME.getByRawId(j);
			if (biome == null || biome2 == null) {
				return false;
			} else if (biome != Biomes.WOODED_BADLANDS_PLATEAU && biome != Biomes.BADLANDS_PLATEAU) {
				return biome.getCategory() != Biome.Category.NONE && biome2.getCategory() != Biome.Category.NONE && biome.getCategory() == biome2.getCategory()
					? true
					: biome == biome2;
			} else {
				return biome2 == Biomes.WOODED_BADLANDS_PLATEAU || biome2 == Biomes.BADLANDS_PLATEAU;
			}
		}
	}

	protected static boolean method_17857(int i) {
		return i == field_19607
			|| i == field_19608
			|| i == field_19609
			|| i == field_19610
			|| i == field_19611
			|| i == field_19612
			|| i == field_19613
			|| i == field_19614
			|| i == field_19615
			|| i == field_19616;
	}

	protected static boolean method_17863(int i) {
		return i == field_19607 || i == field_19608 || i == field_19609 || i == field_19610 || i == field_19611;
	}
}
