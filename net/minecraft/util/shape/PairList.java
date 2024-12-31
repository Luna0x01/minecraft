package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface PairList {
	DoubleList getPairs();

	boolean forEachPair(PairList.Consumer consumer);

	public interface Consumer {
		boolean merge(int i, int j, int k);
	}
}
