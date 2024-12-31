package net.minecraft;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class class_4094 extends AbstractDoubleList implements class_4090 {
	private final DoubleList field_19844;
	private final DoubleList field_19845;
	private final boolean field_19846;

	public class_4094(DoubleList doubleList, DoubleList doubleList2, boolean bl) {
		this.field_19844 = doubleList;
		this.field_19845 = doubleList2;
		this.field_19846 = bl;
	}

	public int size() {
		return this.field_19844.size() + this.field_19845.size();
	}

	@Override
	public boolean method_18041(class_4090.class_4091 arg) {
		return this.field_19846 ? this.method_18043((i, j, k) -> arg.merge(j, i, k)) : this.method_18043(arg);
	}

	private boolean method_18043(class_4090.class_4091 arg) {
		int i = this.field_19844.size() - 1;

		for (int j = 0; j < i; j++) {
			if (!arg.merge(j, -1, j)) {
				return false;
			}
		}

		if (!arg.merge(i, -1, i)) {
			return false;
		} else {
			for (int k = 0; k < this.field_19845.size(); k++) {
				if (!arg.merge(i, k, i + 1 + k)) {
					return false;
				}
			}

			return true;
		}
	}

	public double getDouble(int i) {
		return i < this.field_19844.size() ? this.field_19844.getDouble(i) : this.field_19845.getDouble(i - this.field_19844.size());
	}

	@Override
	public DoubleList method_18040() {
		return this;
	}
}
