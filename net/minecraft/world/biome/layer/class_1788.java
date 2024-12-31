package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4057;

public enum class_1788 implements class_4057 {
	INSTANCE;

	@Override
	public int method_17892(class_4040 arg, int i, int j, int k, int l, int m) {
		return class_4046.method_17863(m)
				&& class_4046.method_17863(i)
				&& class_4046.method_17863(j)
				&& class_4046.method_17863(l)
				&& class_4046.method_17863(k)
				&& arg.method_17850(2) == 0
			? 1
			: m;
	}
}
