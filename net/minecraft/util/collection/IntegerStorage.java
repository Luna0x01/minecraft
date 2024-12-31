package net.minecraft.util.collection;

public class IntegerStorage {
	private static final Integer[] INTS = new Integer[65535];

	public static Integer get(int i) {
		return i > 0 && i < INTS.length ? INTS[i] : i;
	}

	static {
		int i = 0;

		for (int j = INTS.length; i < j; i++) {
			INTS[i] = i;
		}
	}
}
