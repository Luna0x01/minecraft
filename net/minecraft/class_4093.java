package net.minecraft;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class class_4093 extends AbstractDoubleList {
	private final int field_19842;
	private final int field_19843;

	class_4093(int i, int j) {
		this.field_19842 = i;
		this.field_19843 = j;
	}

	public double getDouble(int i) {
		return (double)(this.field_19843 + i);
	}

	public int size() {
		return this.field_19842 + 1;
	}
}
