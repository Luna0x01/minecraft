package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public abstract class AbstractTeam {
	public boolean isEqual(@Nullable AbstractTeam team) {
		return team == null ? false : this == team;
	}

	public abstract String getName();

	public abstract Text method_18122(Text text);

	public abstract boolean shouldShowFriendlyInvisibles();

	public abstract boolean isFriendlyFireAllowed();

	public abstract AbstractTeam.VisibilityRule getNameTagVisibilityRule();

	public abstract Formatting method_12130();

	public abstract Collection<String> getPlayerList();

	public abstract AbstractTeam.VisibilityRule getDeathMessageVisibilityRule();

	public abstract AbstractTeam.CollisionRule method_12129();

	public static enum CollisionRule {
		ALWAYS("always", 0),
		NEVER("never", 1),
		PUSH_OTHER_TEAMS("pushOtherTeams", 2),
		PUSH_OWN_TEAM("pushOwnTeam", 3);

		private static final Map<String, AbstractTeam.CollisionRule> field_19877 = (Map<String, AbstractTeam.CollisionRule>)Arrays.stream(values())
			.collect(Collectors.toMap(collisionRule -> collisionRule.name, collisionRule -> collisionRule));
		public final String name;
		public final int id;

		@Nullable
		public static AbstractTeam.CollisionRule method_12132(String string) {
			return (AbstractTeam.CollisionRule)field_19877.get(string);
		}

		private CollisionRule(String string2, int j) {
			this.name = string2;
			this.id = j;
		}

		public Text method_18124() {
			return new TranslatableText("team.collision." + this.name);
		}
	}

	public static enum VisibilityRule {
		ALWAYS("always", 0),
		NEVER("never", 1),
		HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
		HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

		private static final Map<String, AbstractTeam.VisibilityRule> field_19878 = (Map<String, AbstractTeam.VisibilityRule>)Arrays.stream(values())
			.collect(Collectors.toMap(visibilityRule -> visibilityRule.name, visibilityRule -> visibilityRule));
		public final String name;
		public final int id;

		@Nullable
		public static AbstractTeam.VisibilityRule getRuleByName(String name) {
			return (AbstractTeam.VisibilityRule)field_19878.get(name);
		}

		private VisibilityRule(String string2, int j) {
			this.name = string2;
			this.id = j;
		}

		public Text method_18127() {
			return new TranslatableText("team.visibility." + this.name);
		}
	}
}
