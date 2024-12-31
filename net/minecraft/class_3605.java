package net.minecraft;

public enum class_3605 {
	EXTREMELY_HIGH(-3),
	VERY_HIGH(-2),
	HIGH(-1),
	NORMAL(0),
	LOW(1),
	VERY_LOW(2),
	EXTREMELY_LOW(3);

	private final int field_17529;

	private class_3605(int j) {
		this.field_17529 = j;
	}

	public static class_3605 method_16423(int i) {
		for (class_3605 lv : values()) {
			if (lv.field_17529 == i) {
				return lv;
			}
		}

		return i < EXTREMELY_HIGH.field_17529 ? EXTREMELY_HIGH : EXTREMELY_LOW;
	}

	public int method_16422() {
		return this.field_17529;
	}
}
