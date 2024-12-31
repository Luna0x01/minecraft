package net.minecraft.client.gui.screen.options;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum HandOption {
	LEFT(new TranslatableText("options.mainHand.left")),
	RIGHT(new TranslatableText("options.mainHand.right"));

	private final Text translation;

	private HandOption(Text text) {
		this.translation = text;
	}

	public HandOption method_13037() {
		return this == LEFT ? RIGHT : LEFT;
	}

	public String toString() {
		return this.translation.asUnformattedString();
	}
}
