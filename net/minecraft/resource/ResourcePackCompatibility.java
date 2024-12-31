package net.minecraft.resource;

import net.minecraft.SharedConstants;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum ResourcePackCompatibility {
	field_14223("old"),
	field_14220("new"),
	field_14224("compatible");

	private final Text notification;
	private final Text confirmMessage;

	private ResourcePackCompatibility(String string2) {
		this.notification = new TranslatableText("resourcePack.incompatible." + string2);
		this.confirmMessage = new TranslatableText("resourcePack.incompatible.confirm." + string2);
	}

	public boolean isCompatible() {
		return this == field_14224;
	}

	public static ResourcePackCompatibility from(int i) {
		if (i < SharedConstants.getGameVersion().getPackVersion()) {
			return field_14223;
		} else {
			return i > SharedConstants.getGameVersion().getPackVersion() ? field_14220 : field_14224;
		}
	}

	public Text getNotification() {
		return this.notification;
	}

	public Text getConfirmMessage() {
		return this.confirmMessage;
	}
}
