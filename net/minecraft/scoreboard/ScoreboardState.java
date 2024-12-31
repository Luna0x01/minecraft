package net.minecraft.scoreboard;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardState extends PersistentState {
	private static final Logger LOGGER = LogManager.getLogger();
	private Scoreboard field_5694;
	private NbtCompound field_5695;

	public ScoreboardState() {
		this("scoreboard");
	}

	public ScoreboardState(String string) {
		super(string);
	}

	public void setScoreboard(Scoreboard scoreboard) {
		this.field_5694 = scoreboard;
		if (this.field_5695 != null) {
			this.fromNbt(this.field_5695);
		}
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		if (this.field_5694 == null) {
			this.field_5695 = nbt;
		} else {
			this.method_4912(nbt.getList("Objectives", 10));
			this.field_5694.method_18110(nbt.getList("PlayerScores", 10));
			if (nbt.contains("DisplaySlots", 10)) {
				this.deserializeDisplaySlots(nbt.getCompound("DisplaySlots"));
			}

			if (nbt.contains("Teams", 9)) {
				this.deserializeTeams(nbt.getList("Teams", 10));
			}
		}
	}

	protected void deserializeTeams(NbtList nbtList) {
		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			String string = nbtCompound.getString("Name");
			if (string.length() > 16) {
				string = string.substring(0, 16);
			}

			Team team = this.field_5694.addTeam(string);
			Text text = Text.Serializer.deserializeText(nbtCompound.getString("DisplayName"));
			if (text != null) {
				team.method_18098(text);
			}

			if (nbtCompound.contains("TeamColor", 8)) {
				team.setFormatting(Formatting.byName(nbtCompound.getString("TeamColor")));
			}

			if (nbtCompound.contains("AllowFriendlyFire", 99)) {
				team.setFriendlyFireAllowed(nbtCompound.getBoolean("AllowFriendlyFire"));
			}

			if (nbtCompound.contains("SeeFriendlyInvisibles", 99)) {
				team.setShowFriendlyInvisibles(nbtCompound.getBoolean("SeeFriendlyInvisibles"));
			}

			if (nbtCompound.contains("MemberNamePrefix", 8)) {
				Text text2 = Text.Serializer.deserializeText(nbtCompound.getString("MemberNamePrefix"));
				if (text2 != null) {
					team.method_18100(text2);
				}
			}

			if (nbtCompound.contains("MemberNameSuffix", 8)) {
				Text text3 = Text.Serializer.deserializeText(nbtCompound.getString("MemberNameSuffix"));
				if (text3 != null) {
					team.method_18102(text3);
				}
			}

			if (nbtCompound.contains("NameTagVisibility", 8)) {
				AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRuleByName(nbtCompound.getString("NameTagVisibility"));
				if (visibilityRule != null) {
					team.method_12128(visibilityRule);
				}
			}

			if (nbtCompound.contains("DeathMessageVisibility", 8)) {
				AbstractTeam.VisibilityRule visibilityRule2 = AbstractTeam.VisibilityRule.getRuleByName(nbtCompound.getString("DeathMessageVisibility"));
				if (visibilityRule2 != null) {
					team.setDeathMessageVisibilityRule(visibilityRule2);
				}
			}

			if (nbtCompound.contains("CollisionRule", 8)) {
				AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.method_12132(nbtCompound.getString("CollisionRule"));
				if (collisionRule != null) {
					team.method_9353(collisionRule);
				}
			}

			this.deserializeTeamPlayers(team, nbtCompound.getList("Players", 8));
		}
	}

	protected void deserializeTeamPlayers(Team team, NbtList nbtList) {
		for (int i = 0; i < nbtList.size(); i++) {
			this.field_5694.method_6614(nbtList.getString(i), team);
		}
	}

	protected void deserializeDisplaySlots(NbtCompound nbtCompound) {
		for (int i = 0; i < 19; i++) {
			if (nbtCompound.contains("slot_" + i, 8)) {
				String string = nbtCompound.getString("slot_" + i);
				ScoreboardObjective scoreboardObjective = this.field_5694.getNullableObjective(string);
				this.field_5694.setObjectiveSlot(i, scoreboardObjective);
			}
		}
	}

	protected void method_4912(NbtList nbtList) {
		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			GenericScoreboardCriteria genericScoreboardCriteria = GenericScoreboardCriteria.method_18129(nbtCompound.getString("CriteriaName"));
			if (genericScoreboardCriteria != null) {
				String string = nbtCompound.getString("Name");
				if (string.length() > 16) {
					string = string.substring(0, 16);
				}

				Text text = Text.Serializer.deserializeText(nbtCompound.getString("DisplayName"));
				GenericScoreboardCriteria.class_4104 lv = GenericScoreboardCriteria.class_4104.method_18133(nbtCompound.getString("RenderType"));
				this.field_5694.method_18113(string, genericScoreboardCriteria, text, lv);
			}
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		if (this.field_5694 == null) {
			LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
			return nbt;
		} else {
			nbt.put("Objectives", this.serializeObjectives());
			nbt.put("PlayerScores", this.field_5694.method_18120());
			nbt.put("Teams", this.serializeTeams());
			this.serializeSlots(nbt);
			return nbt;
		}
	}

	protected NbtList serializeTeams() {
		NbtList nbtList = new NbtList();

		for (Team team : this.field_5694.getTeams()) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putString("Name", team.getName());
			nbtCompound.putString("DisplayName", Text.Serializer.serialize(team.method_18101()));
			if (team.method_12130().getColorIndex() >= 0) {
				nbtCompound.putString("TeamColor", team.method_12130().getName());
			}

			nbtCompound.putBoolean("AllowFriendlyFire", team.isFriendlyFireAllowed());
			nbtCompound.putBoolean("SeeFriendlyInvisibles", team.shouldShowFriendlyInvisibles());
			nbtCompound.putString("MemberNamePrefix", Text.Serializer.serialize(team.method_18104()));
			nbtCompound.putString("MemberNameSuffix", Text.Serializer.serialize(team.method_18105()));
			nbtCompound.putString("NameTagVisibility", team.getNameTagVisibilityRule().name);
			nbtCompound.putString("DeathMessageVisibility", team.getDeathMessageVisibilityRule().name);
			nbtCompound.putString("CollisionRule", team.method_12129().name);
			NbtList nbtList2 = new NbtList();

			for (String string : team.getPlayerList()) {
				nbtList2.add((NbtElement)(new NbtString(string)));
			}

			nbtCompound.put("Players", nbtList2);
			nbtList.add((NbtElement)nbtCompound);
		}

		return nbtList;
	}

	protected void serializeSlots(NbtCompound nbtCompound) {
		NbtCompound nbtCompound2 = new NbtCompound();
		boolean bl = false;

		for (int i = 0; i < 19; i++) {
			ScoreboardObjective scoreboardObjective = this.field_5694.getObjectiveForSlot(i);
			if (scoreboardObjective != null) {
				nbtCompound2.putString("slot_" + i, scoreboardObjective.getName());
				bl = true;
			}
		}

		if (bl) {
			nbtCompound.put("DisplaySlots", nbtCompound2);
		}
	}

	protected NbtList serializeObjectives() {
		NbtList nbtList = new NbtList();

		for (ScoreboardObjective scoreboardObjective : this.field_5694.getObjectives()) {
			if (scoreboardObjective.method_4848() != null) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putString("Name", scoreboardObjective.getName());
				nbtCompound.putString("CriteriaName", scoreboardObjective.method_4848().method_4917());
				nbtCompound.putString("DisplayName", Text.Serializer.serialize(scoreboardObjective.method_4849()));
				nbtCompound.putString("RenderType", scoreboardObjective.method_9351().method_18132());
				nbtList.add((NbtElement)nbtCompound);
			}
		}

		return nbtList;
	}
}
