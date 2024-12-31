package net.minecraft.stat;

import net.minecraft.item.Item;
import net.minecraft.text.Text;

public class CraftingStat extends Stat {
	private final Item item;

	public CraftingStat(String string, String string2, Text text, Item item) {
		super(string + string2, text);
		this.item = item;
	}

	public Item getItem() {
		return this.item;
	}
}
