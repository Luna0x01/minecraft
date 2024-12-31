package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.CustomizedWorldProperties;
import net.minecraft.world.level.LevelGeneratorType;

public class SetBaseBiomesLayer extends Layer {
	private Biome[] warmBiomes = new Biome[]{Biome.DESERT, Biome.DESERT, Biome.DESERT, Biome.SAVANNA, Biome.SAVANNA, Biome.PLAINS};
	private Biome[] field_7598 = new Biome[]{Biome.FOREST, Biome.ROOFED_FOREST, Biome.EXTREME_HILLS, Biome.PLAINS, Biome.BIRCH_FOREST, Biome.SWAMPLAND};
	private Biome[] field_7599 = new Biome[]{Biome.FOREST, Biome.EXTREME_HILLS, Biome.TAIGA, Biome.PLAINS};
	private Biome[] coolBiomes = new Biome[]{Biome.ICE_PLAINS, Biome.ICE_PLAINS, Biome.ICE_PLAINS, Biome.COLD_TAIGA};
	private final CustomizedWorldProperties properties;

	public SetBaseBiomesLayer(long l, Layer layer, LevelGeneratorType levelGeneratorType, String string) {
		super(l);
		this.field_172 = layer;
		if (levelGeneratorType == LevelGeneratorType.DEFAULT_1_1) {
			this.warmBiomes = new Biome[]{Biome.DESERT, Biome.FOREST, Biome.EXTREME_HILLS, Biome.SWAMPLAND, Biome.PLAINS, Biome.TAIGA};
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
				} else if (o == Biome.MUSHROOM_ISLAND.id) {
					js[n + m * k] = o;
				} else if (o == 1) {
					if (p > 0) {
						if (this.nextInt(3) == 0) {
							js[n + m * k] = Biome.MESA_PLATEAU.id;
						} else {
							js[n + m * k] = Biome.MESA_PLATEAU_F.id;
						}
					} else {
						js[n + m * k] = this.warmBiomes[this.nextInt(this.warmBiomes.length)].id;
					}
				} else if (o == 2) {
					if (p > 0) {
						js[n + m * k] = Biome.JUNGLE.id;
					} else {
						js[n + m * k] = this.field_7598[this.nextInt(this.field_7598.length)].id;
					}
				} else if (o == 3) {
					if (p > 0) {
						js[n + m * k] = Biome.MEGA_TAIGA.id;
					} else {
						js[n + m * k] = this.field_7599[this.nextInt(this.field_7599.length)].id;
					}
				} else if (o == 4) {
					js[n + m * k] = this.coolBiomes[this.nextInt(this.coolBiomes.length)].id;
				} else {
					js[n + m * k] = Biome.MUSHROOM_ISLAND.id;
				}
			}
		}

		return js;
	}
}
