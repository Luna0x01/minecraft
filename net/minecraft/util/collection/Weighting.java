package net.minecraft.util.collection;

import java.util.List;
import java.util.Random;

public class Weighting {
	public static int getWeightSum(List<? extends Weighting.Weight> pool) {
		int i = 0;
		int j = 0;

		for (int k = pool.size(); j < k; j++) {
			Weighting.Weight weight = (Weighting.Weight)pool.get(j);
			i += weight.weight;
		}

		return i;
	}

	public static <T extends Weighting.Weight> T getRandom(Random random, List<T> pool, int totalWeight) {
		if (totalWeight <= 0) {
			throw new IllegalArgumentException();
		} else {
			int i = random.nextInt(totalWeight);
			return getAt(pool, i);
		}
	}

	public static <T extends Weighting.Weight> T getAt(List<T> pool, int totalWeight) {
		int i = 0;

		for (int j = pool.size(); i < j; i++) {
			T weight = (T)pool.get(i);
			totalWeight -= weight.weight;
			if (totalWeight < 0) {
				return weight;
			}
		}

		return null;
	}

	public static <T extends Weighting.Weight> T getRandom(Random random, List<T> pool) {
		return getRandom(random, pool, getWeightSum(pool));
	}

	public static class Weight {
		protected int weight;

		public Weight(int i) {
			this.weight = i;
		}
	}
}
