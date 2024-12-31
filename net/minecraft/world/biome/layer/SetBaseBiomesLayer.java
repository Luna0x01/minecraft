package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.CustomizedWorldProperties;
import net.minecraft.world.level.LevelGeneratorType;

public class SetBaseBiomesLayer extends Layer {
	private Biome[] warmBiomes = new Biome[]{Biomes.DESERT, Biomes.DESERT, Biomes.DESERT, Biomes.SAVANNA, Biomes.SAVANNA, Biomes.PLAINS};
	private final Biome[] field_7598 = new Biome[]{Biomes.FOREST, Biomes.ROOFED_FOREST, Biomes.EXTREME_HILLS, Biomes.PLAINS, Biomes.BIRCH_FOREST, Biomes.SWAMP};
	private final Biome[] field_7599 = new Biome[]{Biomes.FOREST, Biomes.EXTREME_HILLS, Biomes.TAIGA, Biomes.PLAINS};
	private final Biome[] coolBiomes = new Biome[]{Biomes.ICE_FLATS, Biomes.ICE_FLATS, Biomes.ICE_FLATS, Biomes.TAIGA_COLD};
	private final CustomizedWorldProperties properties;

	public SetBaseBiomesLayer(long l, Layer layer, LevelGeneratorType levelGeneratorType, String string) {
		super(l);
		this.field_172 = layer;
		if (levelGeneratorType == LevelGeneratorType.DEFAULT_1_1) {
			this.warmBiomes = new Biome[]{Biomes.DESERT, Biomes.FOREST, Biomes.EXTREME_HILLS, Biomes.SWAMP, Biomes.PLAINS, Biomes.TAIGA};
			this.properties = null;
		} else if (levelGeneratorType == LevelGeneratorType.CUSTOMIZED) {
			this.properties = CustomizedWorldProperties.Builder.fromJson(string).build();
		} else {
			this.properties = null;
		}
	}

	@Override
	public int[] method_143(int i, int j, int k, int l) {
		int[] is = this.field_172.method_143(i, j, k, l);
		int[] js = IntArrayCache.get(k * l);

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				this.method_145((long)(n + i), (long)(m + j));
				int o = is[n + m * k];
				int p = (o & 3840) >> 8;
				o &= -3841;
				if (this.properties != null && this.properties.fixedBiome >= 0) {
					js[n + m * k] = this.properties.fixedBiome;
				} else if (isOcean(o)) {
					js[n + m * k] = o;
				} else if (o == Biome.getBiomeIndex(Biomes.MUSHROOM_ISLAND)) {
					js[n + m * k] = o;
				} else if (o == 1) {
					if (p > 0) {
						if (this.nextInt(3) == 0) {
							js[n + m * k] = Biome.getBiomeIndex(Biomes.MESA_CLEAR_ROCK);
						} else {
							js[n + m * k] = Biome.getBiomeIndex(Biomes.MESA_ROCK);
						}
					} else {
						js[n + m * k] = Biome.getBiomeIndex(this.warmBiomes[this.nextInt(this.warmBiomes.length)]);
					}
				} else if (o == 2) {
					if (p > 0) {
						js[n + m * k] = Biome.getBiomeIndex(Biomes.JUNGLE);
					} else {
						js[n + m * k] = Biome.getBiomeIndex(this.field_7598[this.nextInt(this.field_7598.length)]);
					}
				} else if (o == 3) {
					if (p > 0) {
						js[n + m * k] = Biome.getBiomeIndex(Biomes.REDWOOD_TAIGA);
					} else {
						js[n + m * k] = Biome.getBiomeIndex(this.field_7599[this.nextInt(this.field_7599.length)]);
					}
				} else if (o == 4) {
					js[n + m * k] = Biome.getBiomeIndex(this.coolBiomes[this.nextInt(this.coolBiomes.length)]);
				} else {
					js[n + m * k] = Biome.getBiomeIndex(Biomes.MUSHROOM_ISLAND);
				}
			}
		}

		return js;
	}
}
