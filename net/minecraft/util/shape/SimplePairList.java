package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public final class SimplePairList implements PairList {
	private final DoubleArrayList valueIndices;
	private final IntArrayList minValues;
	private final IntArrayList maxValues;

	protected SimplePairList(DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
		int i = 0;
		int j = 0;
		double d = Double.NaN;
		int k = doubleList.size();
		int l = doubleList2.size();
		int m = k + l;
		this.valueIndices = new DoubleArrayList(m);
		this.minValues = new IntArrayList(m);
		this.maxValues = new IntArrayList(m);

		while (true) {
			boolean bl3 = i < k;
			boolean bl4 = j < l;
			if (!bl3 && !bl4) {
				if (this.valueIndices.isEmpty()) {
					this.valueIndices.add(Math.min(doubleList.getDouble(k - 1), doubleList2.getDouble(l - 1)));
				}

				return;
			}

			boolean bl5 = bl3 && (!bl4 || doubleList.getDouble(i) < doubleList2.getDouble(j) + 1.0E-7);
			double e = bl5 ? doubleList.getDouble(i++) : doubleList2.getDouble(j++);
			if ((i != 0 && bl3 || bl5 || bl2) && (j != 0 && bl4 || !bl5 || bl)) {
				if (!(d >= e - 1.0E-7)) {
					this.minValues.add(i - 1);
					this.maxValues.add(j - 1);
					this.valueIndices.add(e);
					d = e;
				} else if (!this.valueIndices.isEmpty()) {
					this.minValues.set(this.minValues.size() - 1, i - 1);
					this.maxValues.set(this.maxValues.size() - 1, j - 1);
				}
			}
		}
	}

	@Override
	public boolean forEachPair(PairList.Consumer consumer) {
		for (int i = 0; i < this.valueIndices.size() - 1; i++) {
			if (!consumer.merge(this.minValues.getInt(i), this.maxValues.getInt(i), i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public DoubleList getPairs() {
		return this.valueIndices;
	}
}
