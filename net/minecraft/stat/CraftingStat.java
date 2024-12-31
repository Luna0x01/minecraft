package net.minecraft.stat;

import net.minecraft.item.Item;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.text.Text;

public class CraftingStat extends Stat {
	private final Item item;

	public CraftingStat(String string, String string2, Text text, Item item) {
		super(string + string2, text);
		this.item = item;
		int i = Item.getRawId(item);
		if (i != 0) {
			ScoreboardCriterion.OBJECTIVES.put(string + i, this.getCriterion());
		}
	}

	public Item getItem() {
		return this.item;
	}
}
