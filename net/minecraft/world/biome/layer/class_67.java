package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4054;

public enum class_67 implements class_4054 {
	INSTANCE;

	@Override
	public int method_17889(class_4040 arg, int i, int j, int k, int l, int m) {
		if (!class_4046.method_17863(m) || class_4046.method_17863(l) && class_4046.method_17863(k) && class_4046.method_17863(i) && class_4046.method_17863(j)) {
			if (!class_4046.method_17863(m)
				&& (class_4046.method_17863(l) || class_4046.method_17863(i) || class_4046.method_17863(k) || class_4046.method_17863(j))
				&& arg.method_17850(5) == 0) {
				if (class_4046.method_17863(l)) {
					return m == 4 ? 4 : l;
				}

				if (class_4046.method_17863(i)) {
					return m == 4 ? 4 : i;
				}

				if (class_4046.method_17863(k)) {
					return m == 4 ? 4 : k;
				}

				if (class_4046.method_17863(j)) {
					return m == 4 ? 4 : j;
				}
			}

			return m;
		} else {
			int n = 1;
			int o = 1;
			if (!class_4046.method_17863(l) && arg.method_17850(n++) == 0) {
				o = l;
			}

			if (!class_4046.method_17863(k) && arg.method_17850(n++) == 0) {
				o = k;
			}

			if (!class_4046.method_17863(i) && arg.method_17850(n++) == 0) {
				o = i;
			}

			if (!class_4046.method_17863(j) && arg.method_17850(n++) == 0) {
				o = j;
			}

			if (arg.method_17850(3) == 0) {
				return o;
			} else {
				return o == 4 ? 4 : m;
			}
		}
	}
}
