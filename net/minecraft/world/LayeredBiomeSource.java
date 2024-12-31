package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.layer.Layer;
import net.minecraft.world.level.LevelGeneratorType;

public class LayeredBiomeSource {
	private Layer layer;
	private Layer field_4714;
	private BiomeCache field_4715 = new BiomeCache(this);
	private List<Biome> biomes;
	private String field_9239 = "";

	protected LayeredBiomeSource() {
		this.biomes = Lists.newArrayList();
		this.biomes.add(Biome.FOREST);
		this.biomes.add(Biome.PLAINS);
		this.biomes.add(Biome.TAIGA);
		this.biomes.add(Biome.TAIGA_HILLS);
		this.biomes.add(Biome.FOREST_HILLS);
		this.biomes.add(Biome.JUNGLE);
		this.biomes.add(Biome.JUNGLE_HILLS);
	}

	public LayeredBiomeSource(long l, LevelGeneratorType levelGeneratorType, String string) {
		this();
		this.field_9239 = string;
		Layer[] layers = Layer.init(l, levelGeneratorType, string);
		this.layer = layers[0];
		this.field_4714 = layers[1];
	}

	public LayeredBiomeSource(World world) {
		this(world.getSeed(), world.getLevelProperties().getGeneratorType(), world.getLevelProperties().getGeneratorOptions());
	}

	public List<Biome> getBiomes() {
		return this.biomes;
	}

	public Biome getBiomeAt(BlockPos pos) {
		return this.getBiomeAt(pos, null);
	}

	public Biome getBiomeAt(BlockPos pos, Biome biome) {
		return this.field_4715.method_3843(pos.getX(), pos.getZ(), biome);
	}

	public float[] method_3856(float[] fs, int x, int z, int w, int h) {
		IntArrayCache.clear();
		if (fs == null || fs.length < w * h) {
			fs = new float[w * h];
		}

		int[] is = this.field_4714.method_143(x, z, w, h);

		for (int i = 0; i < w * h; i++) {
			try {
				float f = (float)Biome.getBiomeById(is[i], Biome.DEFAULT).getDownfall() / 65536.0F;
				if (f > 1.0F) {
					f = 1.0F;
				}

				fs[i] = f;
			} catch (Throwable var11) {
				CrashReport crashReport = CrashReport.create(var11, "Invalid Biome id");
				CrashReportSection crashReportSection = crashReport.addElement("DownfallBlock");
				crashReportSection.add("biome id", i);
				crashReportSection.add("downfalls[] size", fs.length);
				crashReportSection.add("x", x);
				crashReportSection.add("z", z);
				crashReportSection.add("w", w);
				crashReportSection.add("h", h);
				throw new CrashException(crashReport);
			}
		}

		return fs;
	}

	public float method_3852(float f, int i) {
		return f;
	}

	public Biome[] method_3857(Biome[] biomes, int i, int j, int k, int l) {
		IntArrayCache.clear();
		if (biomes == null || biomes.length < k * l) {
			biomes = new Biome[k * l];
		}

		int[] is = this.layer.method_143(i, j, k, l);

		try {
			for (int m = 0; m < k * l; m++) {
				biomes[m] = Biome.getBiomeById(is[m], Biome.DEFAULT);
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

	public Biome[] method_3861(Biome[] biomes, int i, int j, int k, int l) {
		return this.method_3858(biomes, i, j, k, l, true);
	}

	public Biome[] method_3858(Biome[] biomes, int i, int j, int k, int l, boolean bl) {
		IntArrayCache.clear();
		if (biomes == null || biomes.length < k * l) {
			biomes = new Biome[k * l];
		}

		if (bl && k == 16 && l == 16 && (i & 15) == 0 && (j & 15) == 0) {
			Biome[] biomes2 = this.field_4715.method_3844(i, j);
			System.arraycopy(biomes2, 0, biomes, 0, k * l);
			return biomes;
		} else {
			int[] is = this.field_4714.method_143(i, j, k, l);

			for (int m = 0; m < k * l; m++) {
				biomes[m] = Biome.getBiomeById(is[m], Biome.DEFAULT);
			}

			return biomes;
		}
	}

	public boolean isValid(int x, int z, int radius, List<Biome> biomes) {
		IntArrayCache.clear();
		int i = x - radius >> 2;
		int j = z - radius >> 2;
		int k = x + radius >> 2;
		int l = z + radius >> 2;
		int m = k - i + 1;
		int n = l - j + 1;
		int[] is = this.layer.method_143(i, j, m, n);

		try {
			for (int o = 0; o < m * n; o++) {
				Biome biome = Biome.byId(is[o]);
				if (!biomes.contains(biome)) {
					return false;
				}
			}

			return true;
		} catch (Throwable var15) {
			CrashReport crashReport = CrashReport.create(var15, "Invalid Biome id");
			CrashReportSection crashReportSection = crashReport.addElement("Layer");
			crashReportSection.add("Layer", this.layer.toString());
			crashReportSection.add("x", x);
			crashReportSection.add("z", z);
			crashReportSection.add("radius", radius);
			crashReportSection.add("allowed", biomes);
			throw new CrashException(crashReport);
		}
	}

	public BlockPos method_3855(int i, int j, int k, List<Biome> list, Random random) {
		IntArrayCache.clear();
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		int[] is = this.layer.method_143(l, m, p, q);
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

	public void method_3859() {
		this.field_4715.method_3840();
	}
}
