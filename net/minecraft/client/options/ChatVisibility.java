package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;

public enum ChatVisibility {
	field_7538(0, "options.chat.visibility.full"),
	field_7539(1, "options.chat.visibility.system"),
	field_7536(2, "options.chat.visibility.hidden");

	private static final ChatVisibility[] field_7534 = (ChatVisibility[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(ChatVisibility::getId))
		.toArray(ChatVisibility[]::new);
	private final int id;
	private final String key;

	private ChatVisibility(int j, String string2) {
		this.id = j;
		this.key = string2;
	}

	public int getId() {
		return this.id;
	}

	public String getTranslationKey() {
		return this.key;
	}

	public static ChatVisibility byId(int i) {
		return field_7534[MathHelper.floorMod(i, field_7534.length)];
	}
}
