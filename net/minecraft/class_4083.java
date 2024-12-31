package net.minecraft;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

class class_4083 extends AbstractDoubleList {
	private final int field_19829;

	class_4083(int i) {
		this.field_19829 = i;
	}

	public double getDouble(int i) {
		return (double)i / (double)this.field_19829;
	}

	public int size() {
		return this.field_19829 + 1;
	}
}
