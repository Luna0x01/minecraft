package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Difficulty {
	PEACEFUL(0, "peaceful"),
	EASY(1, "easy"),
	NORMAL(2, "normal"),
	HARD(3, "hard");

	private static final Difficulty[] DIFFICULTIES = (Difficulty[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(Difficulty::getId))
		.toArray(Difficulty[]::new);
	private final int id;
	private final String name;

	private Difficulty(int j, String string2) {
		this.id = j;
		this.name = string2;
	}

	public int getId() {
		return this.id;
	}

	public Text method_15537() {
		return new TranslatableText("options.difficulty." + this.name);
	}

	public static Difficulty byOrdinal(int ordinal) {
		return DIFFICULTIES[ordinal % DIFFICULTIES.length];
	}

	public String getName() {
		return this.name;
	}
}
