package net.minecraft.world.biome.layer;

import java.util.concurrent.Callable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.CustomizedWorldProperties;
import net.minecraft.world.level.LevelGeneratorType;

public abstract class Layer {
	private long field_173;
	protected Layer field_172;
	private long field_174;
	protected long field_175;

	public static Layer[] init(long seed, LevelGeneratorType type, String string) {
		Layer layer = new class_74(1L);
		layer = new class_71(2000L, layer);
		layer = new class_67(1L, layer);
		layer = new class_84(2001L, layer);
		layer = new class_67(2L, layer);
		layer = new class_67(50L, layer);
		layer = new class_67(70L, layer);
		layer = new class_1788(2L, layer);
		layer = new class_69(2L, layer);
		layer = new class_67(3L, layer);
		layer = new EdgeLayer(2L, layer, EdgeLayer.Type.COOL_WARM);
		layer = new EdgeLayer(2L, layer, EdgeLayer.Type.HEAT_ICE);
		layer = new EdgeLayer(3L, layer, EdgeLayer.Type.SPECIAL);
		layer = new class_84(2002L, layer);
		layer = new class_84(2003L, layer);
		layer = new class_67(4L, layer);
		layer = new MushroomIslandLayer(5L, layer);
		layer = new class_1781(4L, layer);
		layer = class_84.method_148(1000L, layer, 0);
		CustomizedWorldProperties customizedWorldProperties = null;
		int i = 4;
		int j = i;
		if (type == LevelGeneratorType.CUSTOMIZED && string.length() > 0) {
			customizedWorldProperties = CustomizedWorldProperties.Builder.fromJson(string).build();
			i = customizedWorldProperties.biomeSize;
			j = customizedWorldProperties.riverSize;
		}

		if (type == LevelGeneratorType.LARGE_BIOMES) {
			i = 6;
		}

		Layer layer2 = class_84.method_148(1000L, layer, 0);
		Layer var32 = new class_77(100L, layer2);
		Layer layer3 = new SetBaseBiomesLayer(200L, layer, type, string);
		Layer var37 = class_84.method_148(1000L, layer3, 2);
		Layer var38 = new XBiomeLayer(1000L, var37);
		Layer layer4 = class_84.method_148(1000L, var32, 2);
		Layer var39 = new AddHillsLayer(1000L, var38, layer4);
		layer2 = class_84.method_148(1000L, var32, 2);
		layer2 = class_84.method_148(1000L, layer2, j);
		Layer var35 = new AddRiverLayer(1L, layer2);
		Layer var36 = new class_81(1000L, var35);
		layer3 = new class_82(1001L, var39);

		for (int k = 0; k < i; k++) {
			layer3 = new class_84((long)(1000 + k), layer3);
			if (k == 0) {
				layer3 = new class_67(3L, layer3);
			}

			if (k == 1 || i == 1) {
				layer3 = new class_80(1000L, layer3);
			}
		}

		Layer var41 = new class_81(1000L, layer3);
		Layer var42 = new class_79(100L, var41, var36);
		Layer layer6 = new class_83(10L, var42);
		var42.method_144(seed);
		layer6.method_144(seed);
		return new Layer[]{var42, layer6, var42};
	}

	public Layer(long l) {
		this.field_175 = l;
		this.field_175 = this.field_175 * (this.field_175 * 6364136223846793005L + 1442695040888963407L);
		this.field_175 += l;
		this.field_175 = this.field_175 * (this.field_175 * 6364136223846793005L + 1442695040888963407L);
		this.field_175 += l;
		this.field_175 = this.field_175 * (this.field_175 * 6364136223846793005L + 1442695040888963407L);
		this.field_175 += l;
	}

	public void method_144(long l) {
		this.field_173 = l;
		if (this.field_172 != null) {
			this.field_172.method_144(l);
		}

		this.field_173 = this.field_173 * (this.field_173 * 6364136223846793005L + 1442695040888963407L);
		this.field_173 = this.field_173 + this.field_175;
		this.field_173 = this.field_173 * (this.field_173 * 6364136223846793005L + 1442695040888963407L);
		this.field_173 = this.field_173 + this.field_175;
		this.field_173 = this.field_173 * (this.field_173 * 6364136223846793005L + 1442695040888963407L);
		this.field_173 = this.field_173 + this.field_175;
	}

	public void method_145(long l, long m) {
		this.field_174 = this.field_173;
		this.field_174 = this.field_174 * (this.field_174 * 6364136223846793005L + 1442695040888963407L);
		this.field_174 += l;
		this.field_174 = this.field_174 * (this.field_174 * 6364136223846793005L + 1442695040888963407L);
		this.field_174 += m;
		this.field_174 = this.field_174 * (this.field_174 * 6364136223846793005L + 1442695040888963407L);
		this.field_174 += l;
		this.field_174 = this.field_174 * (this.field_174 * 6364136223846793005L + 1442695040888963407L);
		this.field_174 += m;
	}

	protected int nextInt(int i) {
		int j = (int)((this.field_174 >> 24) % (long)i);
		if (j < 0) {
			j += i;
		}

		this.field_174 = this.field_174 * (this.field_174 * 6364136223846793005L + 1442695040888963407L);
		this.field_174 = this.field_174 + this.field_173;
		return j;
	}

	public abstract int[] method_143(int i, int j, int k, int l);

	protected static boolean compareBiomes(int biomeAId, int biomeBId) {
		if (biomeAId == biomeBId) {
			return true;
		} else if (biomeAId != Biome.MESA_PLATEAU_F.id && biomeAId != Biome.MESA_PLATEAU.id) {
			final Biome biome = Biome.byId(biomeAId);
			final Biome biome2 = Biome.byId(biomeBId);

			try {
				return biome != null && biome2 != null ? biome.method_6421(biome2) : false;
			} catch (Throwable var7) {
				CrashReport crashReport = CrashReport.create(var7, "Comparing biomes");
				CrashReportSection crashReportSection = crashReport.addElement("Biomes being compared");
				crashReportSection.add("Biome A ID", biomeAId);
				crashReportSection.add("Biome B ID", biomeBId);
				crashReportSection.add("Biome A", new Callable<String>() {
					public String call() throws Exception {
						return String.valueOf(biome);
					}
				});
				crashReportSection.add("Biome B", new Callable<String>() {
					public String call() throws Exception {
						return String.valueOf(biome2);
					}
				});
				throw new CrashException(crashReport);
			}
		} else {
			return biomeBId == Biome.MESA_PLATEAU_F.id || biomeBId == Biome.MESA_PLATEAU.id;
		}
	}

	protected static boolean isOcean(int biomeId) {
		return biomeId == Biome.OCEAN.id || biomeId == Biome.DEEP_OCEAN.id || biomeId == Biome.FROZEN_OCEAN.id;
	}

	protected int getRandomBiome(int... args) {
		return args[this.nextInt(args.length)];
	}

	protected int method_6598(int i, int j, int k, int l) {
		if (j == k && k == l) {
			return j;
		} else if (i == j && i == k) {
			return i;
		} else if (i == j && i == l) {
			return i;
		} else if (i == k && i == l) {
			return i;
		} else if (i == j && k != l) {
			return i;
		} else if (i == k && j != l) {
			return i;
		} else if (i == l && j != k) {
			return i;
		} else if (j == k && i != l) {
			return j;
		} else if (j == l && i != k) {
			return j;
		} else {
			return k == l && i != j ? k : this.getRandomBiome(i, j, k, l);
		}
	}
}
