package net.minecraft.util.collection;

import java.util.Collection;
import java.util.Random;

public class Weighting {
	public static int getRate(Collection<? extends Weighting.Weight> pool) {
		int i = 0;

		for (Weighting.Weight weight : pool) {
			i += weight.weight;
		}

		return i;
	}

	public static <T extends Weighting.Weight> T pickRandomly(Random rand, Collection<T> entries, int rate) {
		if (rate <= 0) {
			throw new IllegalArgumentException();
		} else {
			int i = rand.nextInt(rate);
			return pick(entries, i);
		}
	}

	public static <T extends Weighting.Weight> T pick(Collection<T> entries, int rate) {
		for (T weight : entries) {
			rate -= weight.weight;
			if (rate < 0) {
				return weight;
			}
		}

		return null;
	}

	public static <T extends Weighting.Weight> T rand(Random rand, Collection<T> entries) {
		return pickRandomly(rand, entries, getRate(entries));
	}

	public static class Weight {
		protected int weight;

		public Weight(int i) {
			this.weight = i;
		}
	}
}
