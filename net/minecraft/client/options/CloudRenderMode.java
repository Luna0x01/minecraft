package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;

public enum CloudRenderMode {
	field_18162(0, "options.off"),
	field_18163(1, "options.clouds.fast"),
	field_18164(2, "options.clouds.fancy");

	private static final CloudRenderMode[] RENDER_MODES = (CloudRenderMode[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(CloudRenderMode::getValue))
		.toArray(CloudRenderMode[]::new);
	private final int value;
	private final String translationKey;

	private CloudRenderMode(int j, String string2) {
		this.value = j;
		this.translationKey = string2;
	}

	public int getValue() {
		return this.value;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public static CloudRenderMode getOption(int i) {
		return RENDER_MODES[MathHelper.floorMod(i, RENDER_MODES.length)];
	}
}
