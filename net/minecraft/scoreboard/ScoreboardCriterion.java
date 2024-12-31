package net.minecraft.scoreboard;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public interface ScoreboardCriterion {
	Map<String, ScoreboardCriterion> OBJECTIVES = Maps.newHashMap();
	ScoreboardCriterion DUMMY = new GenericScoreboardCriteria("dummy");
	ScoreboardCriterion TRIGGER = new GenericScoreboardCriteria("trigger");
	ScoreboardCriterion DEATH_COUNT = new GenericScoreboardCriteria("deathCount");
	ScoreboardCriterion PLAYERS_KILLED = new GenericScoreboardCriteria("playerKillCount");
	ScoreboardCriterion TOTAL_KILLED = new GenericScoreboardCriteria("totalKillCount");
	ScoreboardCriterion HEALTH = new HealthScoreboardCriteria("health");
	ScoreboardCriterion[] TEAM_KILLS = new ScoreboardCriterion[]{
		new TeamScoreboardCriteria("teamkill.", Formatting.BLACK),
		new TeamScoreboardCriteria("teamkill.", Formatting.DARK_BLUE),
		new TeamScoreboardCriteria("teamkill.", Formatting.DARK_GREEN),
		new TeamScoreboardCriteria("teamkill.", Formatting.DARK_AQUA),
		new TeamScoreboardCriteria("teamkill.", Formatting.DARK_RED),
		new TeamScoreboardCriteria("teamkill.", Formatting.DARK_PURPLE),
		new TeamScoreboardCriteria("teamkill.", Formatting.GOLD),
		new TeamScoreboardCriteria("teamkill.", Formatting.GRAY),
		new TeamScoreboardCriteria("teamkill.", Formatting.DARK_GRAY),
		new TeamScoreboardCriteria("teamkill.", Formatting.BLUE),
		new TeamScoreboardCriteria("teamkill.", Formatting.GREEN),
		new TeamScoreboardCriteria("teamkill.", Formatting.AQUA),
		new TeamScoreboardCriteria("teamkill.", Formatting.RED),
		new TeamScoreboardCriteria("teamkill.", Formatting.LIGHT_PURPLE),
		new TeamScoreboardCriteria("teamkill.", Formatting.YELLOW),
		new TeamScoreboardCriteria("teamkill.", Formatting.WHITE)
	};
	ScoreboardCriterion[] KILLED_BY_TEAM = new ScoreboardCriterion[]{
		new TeamScoreboardCriteria("killedByTeam.", Formatting.BLACK),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.DARK_BLUE),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.DARK_GREEN),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.DARK_AQUA),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.DARK_RED),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.DARK_PURPLE),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.GOLD),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.GRAY),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.DARK_GRAY),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.BLUE),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.GREEN),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.AQUA),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.RED),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.LIGHT_PURPLE),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.YELLOW),
		new TeamScoreboardCriteria("killedByTeam.", Formatting.WHITE)
	};

	String getName();

	int method_4918(List<PlayerEntity> players);

	boolean method_4919();

	ScoreboardCriterion.RenderType getRenderType();

	public static enum RenderType {
		INTEGER("integer"),
		HEARTS("hearts");

		private static final Map<String, ScoreboardCriterion.RenderType> TYPES = Maps.newHashMap();
		private final String name;

		private RenderType(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}

		public static ScoreboardCriterion.RenderType getByName(String name) {
			ScoreboardCriterion.RenderType renderType = (ScoreboardCriterion.RenderType)TYPES.get(name);
			return renderType == null ? INTEGER : renderType;
		}

		static {
			for (ScoreboardCriterion.RenderType renderType : values()) {
				TYPES.put(renderType.getName(), renderType);
			}
		}
	}
}
