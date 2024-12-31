package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum class_4461 {
	TOO_OLD("old"),
	TOO_NEW("new"),
	COMPATIBLE("compatible");

	private final Text field_21902;
	private final Text field_21903;

	private class_4461(String string2) {
		this.field_21902 = new TranslatableText("resourcePack.incompatible." + string2);
		this.field_21903 = new TranslatableText("resourcePack.incompatible.confirm." + string2);
	}

	public boolean method_21343() {
		return this == COMPATIBLE;
	}

	public static class_4461 method_21344(int i) {
		if (i < 4) {
			return TOO_OLD;
		} else {
			return i > 4 ? TOO_NEW : COMPATIBLE;
		}
	}

	public Text method_21345() {
		return this.field_21902;
	}

	public Text method_21346() {
		return this.field_21903;
	}
}
