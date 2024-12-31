package net.minecraft.client.options;

import java.util.function.BooleanSupplier;
import net.minecraft.client.util.InputUtil;

public class StickyKeyBinding extends KeyBinding {
	private final BooleanSupplier toggleGetter;

	public StickyKeyBinding(String string, int i, String string2, BooleanSupplier booleanSupplier) {
		super(string, InputUtil.Type.field_1668, i, string2);
		this.toggleGetter = booleanSupplier;
	}

	@Override
	public void setPressed(boolean bl) {
		if (this.toggleGetter.getAsBoolean()) {
			if (bl) {
				super.setPressed(!this.isPressed());
			}
		} else {
			super.setPressed(bl);
		}
	}
}
