package net.minecraft.world;

public enum Difficulty {
	PEACEFUL(0, "options.difficulty.peaceful"),
	EASY(1, "options.difficulty.easy"),
	NORMAL(2, "options.difficulty.normal"),
	HARD(3, "options.difficulty.hard");

	private static final Difficulty[] DIFFICULTIES = new Difficulty[values().length];
	private final int id;
	private final String name;

	private Difficulty(int j, String string2) {
		this.id = j;
		this.name = string2;
	}

	public int getId() {
		return this.id;
	}

	public static Difficulty byOrdinal(int ordinal) {
		return DIFFICULTIES[ordinal % DIFFICULTIES.length];
	}

	public String getName() {
		return this.name;
	}

	static {
		for (Difficulty difficulty : values()) {
			DIFFICULTIES[difficulty.id] = difficulty;
		}
	}
}
