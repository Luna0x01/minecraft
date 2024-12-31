package net.minecraft.util;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class OffsetDoubleList extends AbstractDoubleList {
	private final DoubleList field_19847;
	private final double field_19848;

	public OffsetDoubleList(DoubleList doubleList, double d) {
		this.field_19847 = doubleList;
		this.field_19848 = d;
	}

	public double getDouble(int i) {
		return this.field_19847.getDouble(i) + this.field_19848;
	}

	public int size() {
		return this.field_19847.size();
	}
}
