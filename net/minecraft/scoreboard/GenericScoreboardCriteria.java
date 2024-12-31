package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;

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
