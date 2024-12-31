package net.minecraft.util;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.text.BaseText;
import net.minecraft.text.Text;

public class KeyBindComponent extends BaseText {
	public static Function<String, Supplier<String>> field_16261 = string -> () -> string;
	private final String keybind;
	private Supplier<String> field_16263;

	public KeyBindComponent(String string) {
		this.keybind = string;
	}

	@Override
	public String computeValue() {
		if (this.field_16263 == null) {
			this.field_16263 = (Supplier<String>)field_16261.apply(this.keybind);
		}

		return (String)this.field_16263.get();
	}

	public KeyBindComponent copy() {
		KeyBindComponent keyBindComponent = new KeyBindComponent(this.keybind);
		keyBindComponent.setStyle(this.getStyle().deepCopy());

		for (Text text : this.getSiblings()) {
			keyBindComponent.append(text.copy());
		}

		return keyBindComponent;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof KeyBindComponent)) {
			return false;
		} else {
			KeyBindComponent keyBindComponent = (KeyBindComponent)object;
			return this.keybind.equals(keyBindComponent.keybind) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "KeybindComponent{keybind='" + this.keybind + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
	}

	public String getKeybind() {
		return this.keybind;
	}
}
