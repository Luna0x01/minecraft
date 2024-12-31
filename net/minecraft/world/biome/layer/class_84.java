package net.minecraft.world.biome.layer;

import net.minecraft.class_4035;
import net.minecraft.class_4036;
import net.minecraft.class_4039;
import net.minecraft.class_4052;

public enum class_84 implements class_4052 {
	NORMAL,
	FUZZY {
		@Override
		protected int method_17876(class_4039<?> arg, int i, int j, int k, int l) {
			return arg.method_17848(i, j, k, l);
		}
	};

	private class_84() {
	}

	@Override
	public class_4036 method_17893(class_4036 arg) {
		int i = arg.method_17838() >> 1;
		int j = arg.method_17839() >> 1;
		int k = (arg.method_17840() >> 1) + 3;
		int l = (arg.method_17841() >> 1) + 3;
		return new class_4036(i, j, k, l);
	}

	@Override
	public int method_17882(class_4039<?> arg, class_4036 arg2, class_4035 arg3, int i, int j) {
		int k = arg2.method_17838() >> 1;
		int l = arg2.method_17839() >> 1;
		int m = i + arg2.method_17838();
		int n = j + arg2.method_17839();
		int o = (m >> 1) - k;
		int p = o + 1;
		int q = (n >> 1) - l;
		int r = q + 1;
		int s = arg3.method_17837(o, q);
		arg.method_17844((long)(m >> 1 << 1), (long)(n >> 1 << 1));
		int t = m & 1;
		int u = n & 1;
		if (t == 0 && u == 0) {
			return s;
		} else {
			int v = arg3.method_17837(o, r);
			int w = arg.method_17848(s, v);
			if (t == 0 && u == 1) {
				return w;
			} else {
				int x = arg3.method_17837(p, q);
				int y = arg.method_17848(s, x);
				if (t == 1 && u == 0) {
					return y;
				} else {
					int z = arg3.method_17837(p, r);
					return this.method_17876(arg, s, x, v, z);
				}
			}
		}
	}

	protected int method_17876(class_4039<?> arg, int i, int j, int k, int l) {
		if (j == k && k == l) {
			return j;
		} else if (i == j && i == k) {
			return i;
		} else if (i == j && i == l) {
			return i;
		} else if (i == k && i == l) {
			return i;
		} else if (i == j && k != l) {
			return i;
		} else if (i == k && j != l) {
			return i;
		} else if (i == l && j != k) {
			return i;
		} else if (j == k && i != l) {
			return j;
		} else if (j == l && i != k) {
			return j;
		} else {
			return k == l && i != j ? k : arg.method_17848(i, j, k, l);
		}
	}
}
