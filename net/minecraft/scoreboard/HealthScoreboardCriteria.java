package net.minecraft.scoreboard;

public class HealthScoreboardCriteria extends GenericScoreboardCriteria {
	public HealthScoreboardCriteria(String string) {
		super(string);
	}

	@Override
	public boolean method_4919() {
		return true;
	}

	@Override
	public ScoreboardCriterion.RenderType getRenderType() {
		return ScoreboardCriterion.RenderType.HEARTS;
	}
}
