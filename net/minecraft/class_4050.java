package net.minecraft;

public enum class_4050 implements class_4053, class_4058 {
	INSTANCE;

	@Override
	public int method_17888(class_4040 arg, class_4036 arg2, class_4035 arg3, class_4035 arg4, int i, int j) {
		int k = arg3.method_17837(i, j);
		int l = arg4.method_17837(i, j);
		if (!class_4046.method_17857(k)) {
			return k;
		} else {
			int m = 8;
			int n = 4;

			for (int o = -8; o <= 8; o += 4) {
				for (int p = -8; p <= 8; p += 4) {
					int q = arg3.method_17837(i + o, j + p);
					if (!class_4046.method_17857(q)) {
						if (l == class_4046.field_19607) {
							return class_4046.field_19608;
						}

						if (l == class_4046.field_19611) {
							return class_4046.field_19610;
						}
					}
				}
			}

			if (k == class_4046.field_19614) {
				if (l == class_4046.field_19608) {
					return class_4046.field_19613;
				}

				if (l == class_4046.field_19609) {
					return class_4046.field_19614;
				}

				if (l == class_4046.field_19610) {
					return class_4046.field_19615;
				}

				if (l == class_4046.field_19611) {
					return class_4046.field_19616;
				}
			}

			return l;
		}
	}
}
