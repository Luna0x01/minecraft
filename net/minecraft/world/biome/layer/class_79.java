package net.minecraft.world.biome.layer;

import net.minecraft.util.collection.IntArrayCache;
import net.minecraft.world.biome.Biome;

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
			if (is[m] == Biome.OCEAN.id || is[m] == Biome.DEEP_OCEAN.id) {
				ks[m] = is[m];
			} else if (js[m] == Biome.RIVER.id) {
				if (is[m] == Biome.ICE_PLAINS.id) {
					ks[m] = Biome.FROZEN_RIVER.id;
				} else if (is[m] != Biome.MUSHROOM_ISLAND.id && is[m] != Biome.MUSHROOM_ISLAND_SHORE.id) {
					ks[m] = js[m] & 0xFF;
				} else {
					ks[m] = Biome.MUSHROOM_ISLAND_SHORE.id;
				}
			} else {
				ks[m] = is[m];
			}
		}

		return ks;
	}
}
