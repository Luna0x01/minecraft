package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.Formatting;

public class Team extends AbstractTeam {
	private final Scoreboard scoreboardInstance;
	private final String name;
	private final Set<String> playerList = Sets.newHashSet();
	private Text field_19872;
	private Text field_19873 = new LiteralText("");
	private Text field_19874 = new LiteralText("");
	private boolean friendlyFire = true;
	private boolean showFriendlyInvisibles = true;
	private AbstractTeam.VisibilityRule nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS;
	private AbstractTeam.VisibilityRule deathMessageVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS;
	private Formatting currentFormatting = Formatting.RESET;
	private AbstractTeam.CollisionRule field_13263 = AbstractTeam.CollisionRule.ALWAYS;

	public Team(Scoreboard scoreboard, String string) {
		this.scoreboardInstance = scoreboard;
		this.name = string;
		this.field_19872 = new LiteralText(string);
	}

	@Override
	public String getName() {
		return this.name;
	}

	public Text method_18101() {
		return this.field_19872;
	}

	public Text method_18103() {
		Text text = ChatSerializer.method_20188(
			this.field_19872
				.method_20177()
				.styled(style -> style.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(this.name))))
		);
		Formatting formatting = this.method_12130();
		if (formatting != Formatting.RESET) {
			text.formatted(formatting);
		}

		return text;
	}

	public void method_18098(Text text) {
		if (text == null) {
			throw new IllegalArgumentException("Name cannot be null");
		} else {
			this.field_19872 = text;
			this.scoreboardInstance.updateScoreboardTeam(this);
		}
	}

	public void method_18100(@Nullable Text text) {
		this.field_19873 = (Text)(text == null ? new LiteralText("") : text.method_20177());
		this.scoreboardInstance.updateScoreboardTeam(this);
	}

	public Text method_18104() {
		return this.field_19873;
	}

	public void method_18102(@Nullable Text text) {
		this.field_19874 = (Text)(text == null ? new LiteralText("") : text.method_20177());
		this.scoreboardInstance.updateScoreboardTeam(this);
	}

	public Text method_18105() {
		return this.field_19874;
	}

	@Override
	public Collection<String> getPlayerList() {
		return this.playerList;
	}

	@Override
	public Text method_18122(Text text) {
		Text text2 = new LiteralText("").append(this.field_19873).append(text).append(this.field_19874);
		Formatting formatting = this.method_12130();
		if (formatting != Formatting.RESET) {
			text2.formatted(formatting);
		}

		return text2;
	}

	public static Text method_18097(@Nullable AbstractTeam abstractTeam, Text text) {
		return abstractTeam == null ? text.method_20177() : abstractTeam.method_18122(text);
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

	public void method_12128(AbstractTeam.VisibilityRule visibilityRule) {
		this.nameTagVisibilityRule = visibilityRule;
		this.scoreboardInstance.updateScoreboardTeam(this);
	}

	public void setDeathMessageVisibilityRule(AbstractTeam.VisibilityRule rule) {
		this.deathMessageVisibilityRule = rule;
		this.scoreboardInstance.updateScoreboardTeam(this);
	}

	@Override
	public AbstractTeam.CollisionRule method_12129() {
		return this.field_13263;
	}

	public void method_9353(AbstractTeam.CollisionRule collisionRule) {
		this.field_13263 = collisionRule;
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
		this.scoreboardInstance.updateScoreboardTeam(this);
	}

	@Override
	public Formatting method_12130() {
		return this.currentFormatting;
	}
}
