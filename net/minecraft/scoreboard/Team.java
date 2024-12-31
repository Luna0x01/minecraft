package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.util.Formatting;

public class Team extends AbstractTeam {
	private final Scoreboard scoreboardInstance;
	private final String name;
	private final Set<String> playerList = Sets.newHashSet();
	private String displayName;
	private String prefix = "";
	private String suffix = "";
	private boolean friendlyFire = true;
	private boolean showFriendlyInvisibles = true;
	private AbstractTeam.VisibilityRule nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS;
	private AbstractTeam.VisibilityRule deathMessageVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS;
	private Formatting currentFormatting = Formatting.RESET;

	public Team(Scoreboard scoreboard, String string) {
		this.scoreboardInstance = scoreboard;
		this.name = string;
		this.displayName = string;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		if (displayName == null) {
			throw new IllegalArgumentException("Name cannot be null");
		} else {
			this.displayName = displayName;
			this.scoreboardInstance.updateScoreboardTeam(this);
		}
	}

	@Override
	public Collection<String> getPlayerList() {
		return this.playerList;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		if (prefix == null) {
			throw new IllegalArgumentException("Prefix cannot be null");
		} else {
			this.prefix = prefix;
			this.scoreboardInstance.updateScoreboardTeam(this);
		}
	}

	public String getSuffix() {
		return this.suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
		this.scoreboardInstance.updateScoreboardTeam(this);
	}

	@Override
	public String decorateName(String name) {
		return this.getPrefix() + name + this.getSuffix();
	}

	public static String decorateName(AbstractTeam team, String name) {
		return team == null ? name : team.decorateName(name);
	}

	@Override
	public boolean isFriendlyFireAllowed() {
		return this.friendlyFire;
	}

	public void setFriendlyFireAllowed(boolean friendlyFire) {
		this.friendlyFire = friendlyFire;
		this.scoreboardInstance.updateScoreboardTeam(this);
	}

	@Override
	public boolean shouldShowFriendlyInvisibles() {
		return this.showFriendlyInvisibles;
	}

	public void setShowFriendlyInvisibles(boolean showFriendlyInvisible) {
		this.showFriendlyInvisibles = showFriendlyInvisible;
		this.scoreboardInstance.updateScoreboardTeam(this);
	}

	@Override
	public AbstractTeam.VisibilityRule getNameTagVisibilityRule() {
		return this.nameTagVisibilityRule;
	}

	@Override
	public AbstractTeam.VisibilityRule getDeathMessageVisibilityRule() {
		return this.deathMessageVisibilityRule;
	}

	public void setNameTagVisibilityRule(AbstractTeam.VisibilityRule rule) {
		this.nameTagVisibilityRule = rule;
		this.scoreboardInstance.updateScoreboardTeam(this);
	}

	public void setDeathMessageVisibilityRule(AbstractTeam.VisibilityRule rule) {
		this.deathMessageVisibilityRule = rule;
		this.scoreboardInstance.updateScoreboardTeam(this);
	}

	public int getFriendlyFlagsBitwise() {
		int i = 0;
		if (this.isFriendlyFireAllowed()) {
			i |= 1;
		}

		if (this.shouldShowFriendlyInvisibles()) {
			i |= 2;
		}

		return i;
	}

	public void setFriendlyFlagsBitwise(int bit) {
		this.setFriendlyFireAllowed((bit & 1) > 0);
		this.setShowFriendlyInvisibles((bit & 2) > 0);
	}

	public void setFormatting(Formatting formatting) {
		this.currentFormatting = formatting;
	}

	public Formatting getFormatting() {
		return this.currentFormatting;
	}
}
