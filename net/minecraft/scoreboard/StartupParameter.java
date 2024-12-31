package net.minecraft.scoreboard;

import net.minecraft.stat.Stat;

public class StartupParameter extends GenericScoreboardCriteria {
	private final Stat stat;

	public StartupParameter(Stat stat) {
		super(stat.name);
		this.stat = stat;
	}
}
