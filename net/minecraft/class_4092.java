package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

final class class_4092 implements class_4090 {
	private final DoubleArrayList field_19839;
	private final IntArrayList field_19840;
	private final IntArrayList field_19841;

	class_4092(DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
		int i = 0;
		int j = 0;
		double d = Double.NaN;
		int k = doubleList.size();
		int l = doubleList2.size();
		int m = k + l;
		this.field_19839 = new DoubleArrayList(m);
		this.field_19840 = new IntArrayList(m);
		this.field_19841 = new IntArrayList(m);

		while (true) {
			boolean bl3 = i < k;
			boolean bl4 = j < l;
			if (!bl3 && !bl4) {
				if (this.field_19839.isEmpty()) {
					this.field_19839.add(Math.min(doubleList.getDouble(k - 1), doubleList2.getDouble(l - 1)));
				}

				return;
			}

			boolean bl5 = bl3 && (!bl4 || doubleList.getDouble(i) < doubleList2.getDouble(j) + 1.0E-7);
			double e = bl5 ? doubleList.getDouble(i++) : doubleList2.getDouble(j++);
			if ((i != 0 && bl3 || bl5 || bl2) && (j != 0 && bl4 || !bl5 || bl)) {
				if (!(d > e - 1.0E-7)) {
					this.field_19840.add(i - 1);
					this.field_19841.add(j - 1);
					this.field_19839.add(e);
					d = e;
				} else if (!this.field_19839.isEmpty()) {
					this.field_19840.set(this.field_19840.size() - 1, i - 1);
					this.field_19841.set(this.field_19841.size() - 1, j - 1);
				}
			}
		}
	}

	@Override
	public boolean method_18041(class_4090.class_4091 arg) {
		for (int i = 0; i < this.field_19839.size() - 1; i++) {
			if (!arg.merge(this.field_19840.getInt(i), this.field_19841.getInt(i), i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public DoubleList method_18040() {
		return this.field_19839;
	}
}
