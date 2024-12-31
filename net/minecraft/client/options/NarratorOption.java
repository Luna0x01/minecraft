package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;

public enum NarratorOption {
	OFF(0, "options.narrator.off"),
	ALL(1, "options.narrator.all"),
	CHAT(2, "options.narrator.chat"),
	SYSTEM(3, "options.narrator.system");

	private static final NarratorOption[] VALUES = (NarratorOption[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(NarratorOption::getId))
		.toArray(NarratorOption[]::new);
	private final int id;
	private final String translationKey;

	private NarratorOption(int j, String string2) {
		this.id = j;
		this.translationKey = string2;
	}

	public int getId() {
		return this.id;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public static NarratorOption byId(int i) {
		return VALUES[MathHelper.floorMod(i, VALUES.length)];
	}
}
