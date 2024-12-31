package net.minecraft;

import net.minecraft.world.gen.class_1779;

public enum class_4047 implements class_4051 {
	INSTANCE;

	@Override
	public int method_17880(class_4040 arg, class_4036 arg2, int i, int j) {
		class_1779 lv = arg.method_17849();
		double d = lv.method_17724((double)(i + arg2.method_17838()) / 8.0, (double)(j + arg2.method_17839()) / 8.0);
		if (d > 0.4) {
			return class_4046.field_19607;
		} else if (d > 0.2) {
			return class_4046.field_19608;
		} else if (d < -0.4) {
			return class_4046.field_19611;
		} else {
			return d < -0.2 ? class_4046.field_19610 : class_4046.field_19609;
		}
	}
}
