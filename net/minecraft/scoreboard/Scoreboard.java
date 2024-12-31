package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public class Scoreboard {
	private final Map<String, ScoreboardObjective> objectives = Maps.newHashMap();
	private final Map<ScoreboardCriterion, List<ScoreboardObjective>> objectivesByCriterion = Maps.newHashMap();
	private final Map<String, Map<ScoreboardObjective, ScoreboardPlayerScore>> playerObjectives = Maps.newHashMap();
	private final ScoreboardObjective[] objectivesArray = new ScoreboardObjective[19];
	private final Map<String, Team> teams = Maps.newHashMap();
	private final Map<String, Team> teamsByPlayer = Maps.newHashMap();
	private static String[] names;

	@Nullable
	public ScoreboardObjective getNullableObjective(String name) {
		return (ScoreboardObjective)this.objectives.get(name);
	}

	public ScoreboardObjective method_4884(String string, ScoreboardCriterion scoreboardCriterion) {
		if (string.length() > 16) {
			throw new IllegalArgumentException("The objective name '" + string + "' is too long!");
		} else {
			ScoreboardObjective scoreboardObjective = this.getNullableObjective(string);
			if (scoreboardObjective != null) {
				throw new IllegalArgumentException("An objective with the name '" + string + "' already exists!");
			} else {
				scoreboardObjective = new ScoreboardObjective(this, string, scoreboardCriterion);
				List<ScoreboardObjective> list = (List<ScoreboardObjective>)this.objectivesByCriterion.get(scoreboardCriterion);
				if (list == null) {
					list = Lists.newArrayList();
					this.objectivesByCriterion.put(scoreboardCriterion, list);
				}

				list.add(scoreboardObjective);
				this.objectives.put(string, scoreboardObjective);
				this.updateObjective(scoreboardObjective);
				return scoreboardObjective;
			}
		}
	}

	public Collection<ScoreboardObjective> getObjective(ScoreboardCriterion scoreboardCriterion) {
		Collection<ScoreboardObjective> collection = (Collection<ScoreboardObjective>)this.objectivesByCriterion.get(scoreboardCriterion);
		return collection == null ? Lists.newArrayList() : Lists.newArrayList(collection);
	}

	public boolean playerHasObjective(String playerName, ScoreboardObjective objective) {
		Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.get(playerName);
		if (map == null) {
			return false;
		} else {
			ScoreboardPlayerScore scoreboardPlayerScore = (ScoreboardPlayerScore)map.get(objective);
			return scoreboardPlayerScore != null;
		}
	}

	public ScoreboardPlayerScore getPlayerScore(String player, ScoreboardObjective objective) {
		if (player.length() > 40) {
			throw new IllegalArgumentException("The player name '" + player + "' is too long!");
		} else {
			Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.get(player);
			if (map == null) {
				map = Maps.newHashMap();
				this.playerObjectives.put(player, map);
			}

			ScoreboardPlayerScore scoreboardPlayerScore = (ScoreboardPlayerScore)map.get(objective);
			if (scoreboardPlayerScore == null) {
				scoreboardPlayerScore = new ScoreboardPlayerScore(this, objective, player);
				map.put(objective, scoreboardPlayerScore);
			}

			return scoreboardPlayerScore;
		}
	}

	public Collection<ScoreboardPlayerScore> getAllPlayerScores(ScoreboardObjective objective) {
		List<ScoreboardPlayerScore> list = Lists.newArrayList();

		for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
			ScoreboardPlayerScore scoreboardPlayerScore = (ScoreboardPlayerScore)map.get(objective);
			if (scoreboardPlayerScore != null) {
				list.add(scoreboardPlayerScore);
			}
		}

		Collections.sort(list, ScoreboardPlayerScore.field_5683);
		return list;
	}

	public Collection<ScoreboardObjective> getObjectives() {
		return this.objectives.values();
	}

	public Collection<String> getKnownPlayers() {
		return this.playerObjectives.keySet();
	}

	public void resetPlayerScore(String playerName, ScoreboardObjective objective) {
		if (objective == null) {
			Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.remove(playerName);
			if (map != null) {
				this.updatePlayerScore(playerName);
			}
		} else {
			Map<ScoreboardObjective, ScoreboardPlayerScore> map2 = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.get(playerName);
			if (map2 != null) {
				ScoreboardPlayerScore scoreboardPlayerScore = (ScoreboardPlayerScore)map2.remove(objective);
				if (map2.size() < 1) {
					Map<ScoreboardObjective, ScoreboardPlayerScore> map3 = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.remove(playerName);
					if (map3 != null) {
						this.updatePlayerScore(playerName);
					}
				} else if (scoreboardPlayerScore != null) {
					this.updatePlayerScore(playerName, objective);
				}
			}
		}
	}

	public Collection<ScoreboardPlayerScore> method_4897() {
		Collection<Map<ScoreboardObjective, ScoreboardPlayerScore>> collection = this.playerObjectives.values();
		List<ScoreboardPlayerScore> list = Lists.newArrayList();

		for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : collection) {
			list.addAll(map.values());
		}

		return list;
	}

	public Map<ScoreboardObjective, ScoreboardPlayerScore> getPlayerObjectives(String string) {
		Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.get(string);
		if (map == null) {
			map = Maps.newHashMap();
		}

		return map;
	}

	public void removeObjective(ScoreboardObjective objective) {
		this.objectives.remove(objective.getName());

		for (int i = 0; i < 19; i++) {
			if (this.getObjectiveForSlot(i) == objective) {
				this.setObjectiveSlot(i, null);
			}
		}

		List<ScoreboardObjective> list = (List<ScoreboardObjective>)this.objectivesByCriterion.get(objective.getCriterion());
		if (list != null) {
			list.remove(objective);
		}

		for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
			map.remove(objective);
		}

		this.updateRemovedObjective(objective);
	}

	public void setObjectiveSlot(int slot, ScoreboardObjective objective) {
		this.objectivesArray[slot] = objective;
	}

	@Nullable
	public ScoreboardObjective getObjectiveForSlot(int i) {
		return this.objectivesArray[i];
	}

	public Team getTeam(String string) {
		return (Team)this.teams.get(string);
	}

	public Team addTeam(String string) {
		if (string.length() > 16) {
			throw new IllegalArgumentException("The team name '" + string + "' is too long!");
		} else {
			Team team = this.getTeam(string);
			if (team != null) {
				throw new IllegalArgumentException("A team with the name '" + string + "' already exists!");
			} else {
				team = new Team(this, string);
				this.teams.put(string, team);
				this.updateScoreboardTeamAndPlayers(team);
				return team;
			}
		}
	}

	public void removeTeam(Team team) {
		this.teams.remove(team.getName());

		for (String string : team.getPlayerList()) {
			this.teamsByPlayer.remove(string);
		}

		this.updateRemovedTeam(team);
	}

	public boolean addPlayerToTeam(String string, String string2) {
		if (string.length() > 40) {
			throw new IllegalArgumentException("The player name '" + string + "' is too long!");
		} else if (!this.teams.containsKey(string2)) {
			return false;
		} else {
			Team team = this.getTeam(string2);
			if (this.getPlayerTeam(string) != null) {
				this.clearPlayerTeam(string);
			}

			this.teamsByPlayer.put(string, team);
			team.getPlayerList().add(string);
			return true;
		}
	}

	public boolean clearPlayerTeam(String string) {
		Team team = this.getPlayerTeam(string);
		if (team != null) {
			this.removePlayerFromTeam(string, team);
			return true;
		} else {
			return false;
		}
	}

	public void removePlayerFromTeam(String playerName, Team team) {
		if (this.getPlayerTeam(playerName) != team) {
			throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + team.getName() + "'.");
		} else {
			this.teamsByPlayer.remove(playerName);
			team.getPlayerList().remove(playerName);
		}
	}

	public Collection<String> getTeamNames() {
		return this.teams.keySet();
	}

	public Collection<Team> getTeams() {
		return this.teams.values();
	}

	@Nullable
	public Team getPlayerTeam(String string) {
		return (Team)this.teamsByPlayer.get(string);
	}

	public void updateObjective(ScoreboardObjective objective) {
	}

	public void updateExistingObjective(ScoreboardObjective objective) {
	}

	public void updateRemovedObjective(ScoreboardObjective objective) {
	}

	public void updateScore(ScoreboardPlayerScore score) {
	}

	public void updatePlayerScore(String playerName) {
	}

	public void updatePlayerScore(String playerName, ScoreboardObjective objective) {
	}

	public void updateScoreboardTeamAndPlayers(Team team) {
	}

	public void updateScoreboardTeam(Team team) {
	}

	public void updateRemovedTeam(Team team) {
	}

	public static String getDisplaySlotName(int slotId) {
		switch (slotId) {
			case 0:
				return "list";
			case 1:
				return "sidebar";
			case 2:
				return "belowName";
			default:
				if (slotId >= 3 && slotId <= 18) {
					Formatting formatting = Formatting.byColorIndex(slotId - 3);
					if (formatting != null && formatting != Formatting.RESET) {
						return "sidebar.team." + formatting.getName();
					}
				}

				return null;
		}
	}

	public static int getDisplaySlotId(String slotName) {
		if ("list".equalsIgnoreCase(slotName)) {
			return 0;
		} else if ("sidebar".equalsIgnoreCase(slotName)) {
			return 1;
		} else if ("belowName".equalsIgnoreCase(slotName)) {
			return 2;
		} else {
			if (slotName.startsWith("sidebar.team.")) {
				String string = slotName.substring("sidebar.team.".length());
				Formatting formatting = Formatting.byName(string);
				if (formatting != null && formatting.getColorIndex() >= 0) {
					return formatting.getColorIndex() + 3;
				}
			}

			return -1;
		}
	}

	public static String[] getDisplaySlotNames() {
		if (names == null) {
			names = new String[19];

			for (int i = 0; i < 19; i++) {
				names[i] = getDisplaySlotName(i);
			}
		}

		return names;
	}

	public void resetEntityScore(Entity entity) {
		if (entity != null && !(entity instanceof PlayerEntity) && !entity.isAlive()) {
			String string = entity.getEntityName();
			this.resetPlayerScore(string, null);
			this.clearPlayerTeam(string);
		}
	}
}
