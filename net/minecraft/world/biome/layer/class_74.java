package net.minecraft.world.biome.layer;

import net.minecraft.class_4036;
import net.minecraft.class_4040;
import net.minecraft.class_4046;
import net.minecraft.class_4051;

public enum class_74 implements class_4051 {
	INSTANCE;

	@Override
	public int method_17880(class_4040 arg, class_4036 arg2, int i, int j) {
		if (i == -arg2.method_17838()
			&& j == -arg2.method_17839()
			&& arg2.method_17838() > -arg2.method_17840()
			&& arg2.method_17838() <= 0
			&& arg2.method_17839() > -arg2.method_17841()
			&& arg2.method_17839() <= 0) {
			return 1;
		} else {
			return arg.method_17850(10) == 0 ? 1 : class_4046.field_19609;
		}
	}
}
