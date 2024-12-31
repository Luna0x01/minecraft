package net.minecraft.world.biome.layer;

import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4056;

public enum class_69 implements class_4056 {
	INSTANCE;

	@Override
	public int method_17891(class_4040 arg, int i) {
		if (class_4046.method_17863(i)) {
			return i;
		} else {
			int j = arg.method_17850(6);
			if (j == 0) {
				return 4;
			} else {
				return j == 1 ? 3 : 1;
			}
		}
	}
}
