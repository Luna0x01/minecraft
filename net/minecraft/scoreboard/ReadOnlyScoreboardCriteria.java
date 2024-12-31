package net.minecraft.scoreboard;

public class ReadOnlyScoreboardCriteria extends GenericScoreboardCriteria {
	public ReadOnlyScoreboardCriteria(String string) {
		super(string);
	}

	@Override
	public boolean method_4919() {
		return true;
	}
}
