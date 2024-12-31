package net.minecraft.scoreboard;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.Formatting;

public abstract class AbstractTeam {
	public boolean isEqual(@Nullable AbstractTeam team) {
		return team == null ? false : this == team;
	}

	public abstract String getName();

	public abstract String decorateName(String name);

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
		HIDE_FOR_OTHER_TEAMS("pushOtherTeams", 2),
		HIDE_FOR_OWN_TEAM("pushOwnTeam", 3);

		private static final Map<String, AbstractTeam.CollisionRule> field_13270 = Maps.newHashMap();
		public final String name;
		public final int id;

		public static String[] method_12131() {
			return (String[])field_13270.keySet().toArray(new String[field_13270.size()]);
		}

		public static AbstractTeam.CollisionRule method_12132(String string) {
			return (AbstractTeam.CollisionRule)field_13270.get(string);
		}

		private CollisionRule(String string2, int j) {
			this.name = string2;
			this.id = j;
		}

		static {
			for (AbstractTeam.CollisionRule collisionRule : values()) {
				field_13270.put(collisionRule.name, collisionRule);
			}
		}
	}

	public static enum VisibilityRule {
		ALWAYS("always", 0),
		NEVER("never", 1),
		HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
		HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

		private static final Map<String, AbstractTeam.VisibilityRule> RULES = Maps.newHashMap();
		public final String name;
		public final int id;

		public static String[] getValuesAsArray() {
			return (String[])RULES.keySet().toArray(new String[RULES.size()]);
		}

		public static AbstractTeam.VisibilityRule getRuleByName(String name) {
			return (AbstractTeam.VisibilityRule)RULES.get(name);
		}

		private VisibilityRule(String string2, int j) {
			this.name = string2;
			this.id = j;
		}

		static {
			for (AbstractTeam.VisibilityRule visibilityRule : values()) {
				RULES.put(visibilityRule.name, visibilityRule);
			}
		}
	}
}
