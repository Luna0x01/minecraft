package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class DisjointPairList extends AbstractDoubleList implements PairList {
	private final DoubleList first;
	private final DoubleList second;
	private final boolean inverted;

	public DisjointPairList(DoubleList doubleList, DoubleList doubleList2, boolean bl) {
		this.first = doubleList;
		this.second = doubleList2;
		this.inverted = bl;
	}

	public int size() {
		return this.first.size() + this.second.size();
	}

	@Override
	public boolean forEachPair(PairList.Consumer consumer) {
		return this.inverted ? this.iterateSections((i, j, k) -> consumer.merge(j, i, k)) : this.iterateSections(consumer);
	}

	private boolean iterateSections(PairList.Consumer consumer) {
		int i = this.first.size() - 1;

		for (int j = 0; j < i; j++) {
			if (!consumer.merge(j, -1, j)) {
				return false;
			}
		}

		if (!consumer.merge(i, -1, i)) {
			return false;
		} else {
			for (int k = 0; k < this.second.size(); k++) {
				if (!consumer.merge(i, k, i + 1 + k)) {
					return false;
				}
			}

			return true;
		}
	}

	public double getDouble(int i) {
		return i < this.first.size() ? this.first.getDouble(i) : this.second.getDouble(i - this.first.size());
	}

	@Override
	public DoubleList getPairs() {
		return this;
	}
}
