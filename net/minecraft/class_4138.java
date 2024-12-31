package net.minecraft;

import net.minecraft.util.Util;

public enum class_4138 implements class_4135 {
	INSTANCE;

	private static final class_4277 field_20134 = Util.make(new class_4277(class_4277.class_4278.RGBA, 5, 8, false), arg -> {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
				arg.method_19460(j, i, bl ? -1 : 0);
			}
		}

		arg.method_19485();
	});

	@Override
	public int method_18472() {
		return 5;
	}

	@Override
	public int method_18474() {
		return 8;
	}

	@Override
	public float getAdvance() {
		return 6.0F;
	}

	@Override
	public float method_18476() {
		return 1.0F;
	}

	@Override
	public void method_18473(int i, int j) {
		field_20134.method_19466(0, i, j, false);
	}

	@Override
	public boolean method_18475() {
		return true;
	}
}
