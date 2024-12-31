package net.minecraft.world.biome.layer;

import net.minecraft.class_4035;
import net.minecraft.class_4036;
import net.minecraft.class_4039;
import net.minecraft.class_4052;

public enum class_83 implements class_4052 {
	INSTANCE;

	@Override
	public int method_17882(class_4039<?> arg, class_4036 arg2, class_4035 arg3, int i, int j) {
		int k = i + arg2.method_17838() - 2;
		int l = j + arg2.method_17839() - 2;
		int m = arg2.method_17838() >> 2;
		int n = arg2.method_17839() >> 2;
		int o = (k >> 2) - m;
		int p = (l >> 2) - n;
		arg.method_17844((long)(o + m << 2), (long)(p + n << 2));
		double d = ((double)arg.method_17850(1024) / 1024.0 - 0.5) * 3.6;
		double e = ((double)arg.method_17850(1024) / 1024.0 - 0.5) * 3.6;
		arg.method_17844((long)(o + m + 1 << 2), (long)(p + n << 2));
		double f = ((double)arg.method_17850(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		double g = ((double)arg.method_17850(1024) / 1024.0 - 0.5) * 3.6;
		arg.method_17844((long)(o + m << 2), (long)(p + n + 1 << 2));
		double h = ((double)arg.method_17850(1024) / 1024.0 - 0.5) * 3.6;
		double q = ((double)arg.method_17850(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		arg.method_17844((long)(o + m + 1 << 2), (long)(p + n + 1 << 2));
		double r = ((double)arg.method_17850(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		double s = ((double)arg.method_17850(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
		int t = k & 3;
		int u = l & 3;
		double v = ((double)u - e) * ((double)u - e) + ((double)t - d) * ((double)t - d);
		double w = ((double)u - g) * ((double)u - g) + ((double)t - f) * ((double)t - f);
		double x = ((double)u - q) * ((double)u - q) + ((double)t - h) * ((double)t - h);
		double y = ((double)u - s) * ((double)u - s) + ((double)t - r) * ((double)t - r);
		if (v < w && v < x && v < y) {
			return arg3.method_17837(o + 0, p + 0);
		} else if (w < v && w < x && w < y) {
			return arg3.method_17837(o + 1, p + 0) & 0xFF;
		} else {
			return x < v && x < w && x < y ? arg3.method_17837(o + 0, p + 1) : arg3.method_17837(o + 1, p + 1) & 0xFF;
		}
	}

	@Override
	public class_4036 method_17893(class_4036 arg) {
		int i = arg.method_17838() >> 2;
		int j = arg.method_17839() >> 2;
		int k = (arg.method_17840() >> 2) + 2;
		int l = (arg.method_17841() >> 2) + 2;
		return new class_4036(i, j, k, l);
	}
}
