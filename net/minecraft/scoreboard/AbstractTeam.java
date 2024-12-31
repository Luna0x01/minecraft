package net.minecraft.scoreboard;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractTeam {
	public boolean isEqual(AbstractTeam team) {
		return team == null ? false : this == team;
	}

	public abstract String getName();

	public abstract String decorateName(String name);

	public abstract boolean shouldShowFriendlyInvisibles();

	public abstract boolean isFriendlyFireAllowed();

	public abstract AbstractTeam.VisibilityRule getNameTagVisibilityRule();

	public abstract Collection<String> getPlayerList();

	public abstract AbstractTeam.VisibilityRule getDeathMessageVisibilityRule();

	public static enum VisibilityRule {
		ALWAYS("always", 0),
		NEVER("never", 1),
		HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
		HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

		private static Map<String, AbstractTeam.VisibilityRule> RULES = Maps.newHashMap();
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
