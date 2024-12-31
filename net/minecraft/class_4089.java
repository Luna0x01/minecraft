package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class class_4089 implements class_4090 {
	private final DoubleList field_19838;

	public class_4089(DoubleList doubleList) {
		this.field_19838 = doubleList;
	}

	@Override
	public boolean method_18041(class_4090.class_4091 arg) {
		for (int i = 0; i <= this.field_19838.size(); i++) {
			if (!arg.merge(i, i, i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public DoubleList method_18040() {
		return this.field_19838;
	}
}
