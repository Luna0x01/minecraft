package net.minecraft.world.biome.layer;

import javax.annotation.Nullable;
import net.minecraft.class_4036;
import net.minecraft.class_4037;
import net.minecraft.class_4038;
import net.minecraft.world.biome.Biome;

public class Layer {
	private final class_4037<class_4038> field_19606;

	public Layer(class_4037<class_4038> arg) {
		this.field_19606 = arg;
	}

	public Biome[] method_17856(int i, int j, int k, int l, @Nullable Biome biome) {
		class_4036 lv = new class_4036(i, j, k, l);
		class_4038 lv2 = this.field_19606.make(lv);
		Biome[] biomes = new Biome[k * l];

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				biomes[n + m * k] = Biome.getByRawIdOrDefault(lv2.method_17837(n, m), biome);
			}
		}

		return biomes;
	}
}
