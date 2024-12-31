package net.minecraft.scoreboard;

public class GenericScoreboardCriteria implements ScoreboardCriterion {
	private final String name;

	public GenericScoreboardCriteria(String string) {
		this.name = string;
		ScoreboardCriterion.OBJECTIVES.put(string, this);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean method_4919() {
		return false;
	}

	@Override
	public ScoreboardCriterion.RenderType getRenderType() {
		return ScoreboardCriterion.RenderType.INTEGER;
	}
}
