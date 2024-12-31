package net.minecraft.block.enums;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.StringIdentifiable;

public enum StructureBlockMode implements StringIdentifiable {
	SAVE("save"),
	LOAD("load"),
	CORNER("corner"),
	DATA("data");

	private final String name;
	private final Text field_26444;

	private StructureBlockMode(String name) {
		this.name = name;
		this.field_26444 = new TranslatableText("structure_block.mode_info." + name);
	}

	@Override
	public String asString() {
		return this.name;
	}

	public Text method_30844() {
		return this.field_26444;
	}
}
