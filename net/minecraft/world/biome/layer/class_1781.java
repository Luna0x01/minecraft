package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4057;

public enum class_1781 implements class_4057 {
	INSTANCE;

	@Override
	public int method_17892(class_4040 arg, int i, int j, int k, int l, int m) {
		if (class_4046.method_17863(m)) {
			int n = 0;
			if (class_4046.method_17863(i)) {
				n++;
			}

			if (class_4046.method_17863(j)) {
				n++;
			}

			if (class_4046.method_17863(l)) {
				n++;
			}

			if (class_4046.method_17863(k)) {
				n++;
			}

			if (n > 3) {
				if (m == class_4046.field_19607) {
					return class_4046.field_19612;
				}

				if (m == class_4046.field_19608) {
					return class_4046.field_19613;
				}

				if (m == class_4046.field_19609) {
					return class_4046.field_19614;
				}

				if (m == class_4046.field_19610) {
					return class_4046.field_19615;
				}

				if (m == class_4046.field_19611) {
					return class_4046.field_19616;
				}

				return class_4046.field_19614;
			}
		}

		return m;
	}
}
