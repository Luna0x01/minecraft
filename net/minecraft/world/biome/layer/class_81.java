package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4057;

public enum class_81 implements class_4057 {
	INSTANCE;

	@Override
	public int method_17892(class_4040 arg, int i, int j, int k, int l, int m) {
		boolean bl = j == l;
		boolean bl2 = i == k;
		if (bl == bl2) {
			if (bl) {
				return arg.method_17850(2) == 0 ? l : i;
			} else {
				return m;
			}
		} else {
			return bl ? l : i;
		}
	}
}
