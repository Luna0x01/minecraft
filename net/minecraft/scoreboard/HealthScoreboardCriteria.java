package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class HealthScoreboardCriteria extends GenericScoreboardCriteria {
	public HealthScoreboardCriteria(String string) {
		super(string);
	}

	@Override
	public int method_4918(List<PlayerEntity> players) {
		float f = 0.0F;

		for (PlayerEntity playerEntity : players) {
			f += playerEntity.getHealth() + playerEntity.getAbsorption();
		}

		if (players.size() > 0) {
			f /= (float)players.size();
		}

		return MathHelper.ceil(f);
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
