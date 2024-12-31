package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class class_3314 implements class_3318 {
	private static final Text field_16219 = new TranslatableText("tutorial.open_inventory.title");
	private static final Text field_16220 = new TranslatableText("tutorial.open_inventory.description", class_3316.method_14725("inventory"));
	private final class_3316 field_16221;
	private class_3266 field_16222;
	private int field_16223;

	public class_3314(class_3316 arg) {
		this.field_16221 = arg;
	}

	@Override
	public void method_14731() {
		this.field_16223++;
		if (this.field_16221.method_14730() != GameMode.SURVIVAL) {
			this.field_16221.method_14724(class_3319.NONE);
		} else {
			if (this.field_16223 >= 600 && this.field_16222 == null) {
				this.field_16222 = new class_3266(class_3266.class_3267.RECIPE_BOOK, field_16219, field_16220, false);
				this.field_16221.method_14729().method_14462().method_14491(this.field_16222);
			}
		}
	}

	@Override
	public void method_14737() {
		if (this.field_16222 != null) {
			this.field_16222.method_14498();
			this.field_16222 = null;
		}
	}

	@Override
	public void method_14738() {
		this.field_16221.method_14724(class_3319.CRAFT_PLANKS);
	}
}
