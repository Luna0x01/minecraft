package net.minecraft.scoreboard;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ChatSerializer;

public class ScoreboardObjective {
	private final Scoreboard scoreboard;
	private final String name;
	private final GenericScoreboardCriteria field_19865;
	private Text field_19866;
	private GenericScoreboardCriteria.class_4104 field_10270;

	public ScoreboardObjective(
		Scoreboard scoreboard, String string, GenericScoreboardCriteria genericScoreboardCriteria, Text text, GenericScoreboardCriteria.class_4104 arg
	) {
		this.scoreboard = scoreboard;
		this.name = string;
		this.field_19865 = genericScoreboardCriteria;
		this.field_19866 = text;
		this.field_10270 = arg;
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public String getName() {
		return this.name;
	}

	public GenericScoreboardCriteria method_4848() {
		return this.field_19865;
	}

	public Text method_4849() {
		return this.field_19866;
	}

	public Text method_18090() {
		return ChatSerializer.method_20188(
			this.field_19866.method_20177().styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(this.getName()))))
		);
	}

	public void method_18088(Text text) {
		this.field_19866 = text;
		this.scoreboard.updateExistingObjective(this);
	}

	public GenericScoreboardCriteria.class_4104 method_9351() {
		return this.field_10270;
	}

	public void method_9350(GenericScoreboardCriteria.class_4104 arg) {
		this.field_10270 = arg;
		this.scoreboard.updateExistingObjective(this);
	}
}
