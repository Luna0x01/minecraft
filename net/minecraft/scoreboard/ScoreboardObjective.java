package net.minecraft.scoreboard;

public class ScoreboardObjective {
	private final Scoreboard scoreboard;
	private final String name;
	private final ScoreboardCriterion criteria;
	private ScoreboardCriterion.RenderType renderType;
	private String field_5674;

	public ScoreboardObjective(Scoreboard scoreboard, String string, ScoreboardCriterion scoreboardCriterion) {
		this.scoreboard = scoreboard;
		this.name = string;
		this.criteria = scoreboardCriterion;
		this.field_5674 = string;
		this.renderType = scoreboardCriterion.getRenderType();
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public String getName() {
		return this.name;
	}

	public ScoreboardCriterion getCriterion() {
		return this.criteria;
	}

	public String getDisplayName() {
		return this.field_5674;
	}

	public void method_4846(String string) {
		this.field_5674 = string;
		this.scoreboard.updateExistingObjective(this);
	}

	public ScoreboardCriterion.RenderType getRenderType() {
		return this.renderType;
	}

	public void setRenderType(ScoreboardCriterion.RenderType renderType) {
		this.renderType = renderType;
		this.scoreboard.updateExistingObjective(this);
	}
}
