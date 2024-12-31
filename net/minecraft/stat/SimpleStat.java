package net.minecraft.stat;

import net.minecraft.text.Text;

public class SimpleStat extends Stat {
	public SimpleStat(String string, Text text, StatTypeProvider statTypeProvider) {
		super(string, text, statTypeProvider);
	}

	public SimpleStat(String string, Text text) {
		super(string, text);
	}

	@Override
	public Stat addStat() {
		super.addStat();
		Stats.GENERAL.add(this);
		return this;
	}
}
