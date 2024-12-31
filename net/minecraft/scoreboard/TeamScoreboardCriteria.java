package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public class TeamScoreboardCriteria implements ScoreboardCriterion {
	private final String name;

	public TeamScoreboardCriteria(String string, Formatting formatting) {
		this.name = string + formatting.getName();
		ScoreboardCriterion.OBJECTIVES.put(this.name, this);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int method_4918(List<PlayerEntity> players) {
		return 0;
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
