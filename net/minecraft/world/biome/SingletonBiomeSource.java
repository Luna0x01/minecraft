package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.layer.Layer;
import net.minecraft.world.gen.CustomizedWorldProperties;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;

public class SingletonBiomeSource {
	private CustomizedWorldProperties field_15122;
	private Layer field_12462;
	private Layer field_12463;
	private final BiomeCache field_12464 = new BiomeCache(this);
	private final List<Biome> field_12465 = Lists.newArrayList(
		new Biome[]{Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.FOREST_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS}
	);

	protected SingletonBiomeSource() {
	}

	private SingletonBiomeSource(long l, LevelGeneratorType levelGeneratorType, String string) {
		this();
		if (levelGeneratorType == LevelGeneratorType.CUSTOMIZED && !string.isEmpty()) {
			this.field_15122 = CustomizedWorldProperties.Builder.fromJson(string).build();
		}

		Layer[] layers = Layer.method_146(l, levelGeneratorType, this.field_15122);
		this.field_12462 = layers[0];
		this.field_12463 = layers[1];
	}

	public SingletonBiomeSource(LevelProperties levelProperties) {
		this(levelProperties.getSeed(), levelProperties.getGeneratorType(), levelProperties.getGeneratorOptions());
	}

	public List<Biome> method_11532() {
		return this.field_12465;
	}

	public Biome method_11535(BlockPos blockPos) {
		return this.method_11536(blockPos, null);
	}

	public Biome method_11536(BlockPos blockPos, Biome biome) {
		return this.field_12464.method_3843(blockPos.getX(), blockPos.getZ(), biome);
	}

	public float method_11533(float f, int i) {
		return f;
	}

	public Biome[] method_11537(Biome[] biomes, int i, int j, int k, int l) {
		IntArrayCache.clear();
		if (biomes == null || biomes.length < k * l) {
			biomes = new Biome[k * l];
		}

		int[] is = this.field_12462.method_143(i, j, k, l);

		try {
			for (int m = 0; m < k * l; m++) {
				biomes[m] = Biome.getByRawIdOrDefault(is[m], Biomes.DEFAULT);
			}

			return biomes;
		} catch (Throwable var10) {
			CrashReport crashReport = CrashReport.create(var10, "Invalid Biome id");
			CrashReportSection crashReportSection = crashReport.addElement("RawBiomeBlock");
			crashReportSection.add("biomes[] size", biomes.length);
			crashReportSection.add("x", i);
			crashReportSection.add("z", j);
			crashReportSection.add("w", k);
			crashReportSection.add("h", l);
			throw new CrashException(crashReport);
		}
	}

	public Biome[] method_11540(@Nullable Biome[] biomes, int i, int j, int k, int l) {
		return this.method_11538(biomes, i, j, k, l, true);
	}

	public Biome[] method_11538(@Nullable Biome[] biomes, int i, int j, int k, int l, boolean bl) {
		IntArrayCache.clear();
		if (biomes == null || biomes.length < k * l) {
			biomes = new Biome[k * l];
		}

		if (bl && k == 16 && l == 16 && (i & 15) == 0 && (j & 15) == 0) {
			Biome[] biomes2 = this.field_12464.method_3844(i, j);
			System.arraycopy(biomes2, 0, biomes, 0, k * l);
			return biomes;
		} else {
			int[] is = this.field_12463.method_143(i, j, k, l);

			for (int m = 0; m < k * l; m++) {
				biomes[m] = Biome.getByRawIdOrDefault(is[m], Biomes.DEFAULT);
			}

			return biomes;
		}
	}

	public boolean method_3854(int i, int j, int k, List<Biome> list) {
		IntArrayCache.clear();
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		int[] is = this.field_12462.method_143(l, m, p, q);

		try {
			for (int r = 0; r < p * q; r++) {
				Biome biome = Biome.byId(is[r]);
				if (!list.contains(biome)) {
					return false;
				}
			}

			return true;
		} catch (Throwable var15) {
			CrashReport crashReport = CrashReport.create(var15, "Invalid Biome id");
			CrashReportSection crashReportSection = crashReport.addElement("Layer");
			crashReportSection.add("Layer", this.field_12462.toString());
			crashReportSection.add("x", i);
			crashReportSection.add("z", j);
			crashReportSection.add("radius", k);
			crashReportSection.add("allowed", list);
			throw new CrashException(crashReport);
		}
	}

	@Nullable
	public BlockPos method_11534(int i, int j, int k, List<Biome> list, Random random) {
		IntArrayCache.clear();
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		int[] is = this.field_12462.method_143(l, m, p, q);
		BlockPos blockPos = null;
		int r = 0;

		for (int s = 0; s < p * q; s++) {
			int t = l + s % p << 2;
			int u = m + s / p << 2;
			Biome biome = Biome.byId(is[s]);
			if (list.contains(biome) && (blockPos == null || random.nextInt(r + 1) == 0)) {
				blockPos = new BlockPos(t, 0, u);
				r++;
			}
		}

		return blockPos;
	}

	public void method_11539() {
		this.field_12464.method_3840();
	}

	public boolean method_13697() {
		return this.field_15122 != null && this.field_15122.fixedBiome >= 0;
	}

	public Biome method_13698() {
		return this.field_15122 != null && this.field_15122.fixedBiome >= 0 ? Biome.getBiomeFromIndex(this.field_15122.fixedBiome) : null;
	}
}
