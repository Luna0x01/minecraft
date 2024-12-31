package net.minecraft.inventory;

import net.minecraft.text.Text;

public class AnimalInventory extends SimpleInventory {
	public AnimalInventory(String string, int i) {
		super(string, false, i);
	}

	public AnimalInventory(Text text, int i) {
		super(text, i);
	}
}
