package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class DisjointPairList extends AbstractDoubleList implements PairList {
	private final DoubleList first;
	private final DoubleList second;
	private final boolean inverted;

	protected DisjointPairList(DoubleList first, DoubleList second, boolean inverted) {
		this.first = first;
		this.second = second;
		this.inverted = inverted;
	}

	@Override
	public int size() {
		return this.first.size() + this.second.size();
	}

	@Override
	public boolean forEachPair(PairList.Consumer predicate) {
		return this.inverted ? this.iterateSections((i, j, k) -> predicate.merge(j, i, k)) : this.iterateSections(predicate);
	}

	private boolean iterateSections(PairList.Consumer consumer) {
		int i = this.first.size();

		for (int j = 0; j < i; j++) {
			if (!consumer.merge(j, -1, j)) {
				return false;
			}
		}

		int k = this.second.size() - 1;

		for (int l = 0; l < k; l++) {
			if (!consumer.merge(i - 1, l, i + l)) {
				return false;
			}
		}

		return true;
	}

	public double getDouble(int position) {
		return position < this.first.size() ? this.first.getDouble(position) : this.second.getDouble(position - this.first.size());
	}

	@Override
	public DoubleList getPairs() {
		return this;
	}
}
