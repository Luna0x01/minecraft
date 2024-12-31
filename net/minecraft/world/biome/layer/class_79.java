package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_79 extends Layer {
	private Layer field_176;
	private Layer field_177;

	public class_79(long l, Layer layer, Layer layer2) {
		super(l);
		this.field_176 = layer;
		this.field_177 = layer2;
	}

	@Override
	public void method_144(long l) {
		this.field_176.method_144(l);
		this.field_177.method_144(l);
		super.method_144(l);
	}

	@Override
	public int[] method_143(int i, int j, int k, int l) {
		int[] is = this.field_176.method_143(i, j, k, l);
		int[] js = this.field_177.method_143(i, j, k, l);
		int[] ks = IntArrayCache.get(k * l);

		for (int m = 0; m < k * l; m++) {
			if (is[m] == Biome.getBiomeIndex(Biomes.OCEAN) || is[m] == Biome.getBiomeIndex(Biomes.DEEP_OCEAN)) {
				ks[m] = is[m];
			} else if (js[m] == Biome.getBiomeIndex(Biomes.RIVER)) {
				if (is[m] == Biome.getBiomeIndex(Biomes.ICE_FLATS)) {
					ks[m] = Biome.getBiomeIndex(Biomes.FROZEN_RIVER);
				} else if (is[m] != Biome.getBiomeIndex(Biomes.MUSHROOM_ISLAND) && is[m] != Biome.getBiomeIndex(Biomes.MUSHROOM_ISLAND_SHORE)) {
					ks[m] = js[m] & 0xFF;
				} else {
					ks[m] = Biome.getBiomeIndex(Biomes.MUSHROOM_ISLAND_SHORE);
				}
			} else {
				ks[m] = is[m];
			}
		}

		return ks;
	}
}
