package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.class_4317;
import net.minecraft.class_4318;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatUtil;

public class ScoreText extends BaseText {
	private final String name;
	@Nullable
	private final class_4317 field_21515;
	private final String objective;
	private String score = "";

	public ScoreText(String string, String string2) {
		this.name = string;
		this.objective = string2;
		class_4317 lv = null;

		try {
			class_4318 lv2 = new class_4318(new StringReader(string));
			lv = lv2.method_19818();
		} catch (CommandSyntaxException var5) {
		}

		this.field_21515 = lv;
	}

	public String getName() {
		return this.name;
	}

	@Nullable
	public class_4317 method_20195() {
		return this.field_21515;
	}

	public String getObjective() {
		return this.objective;
	}

	public void setScore(String score) {
		this.score = score;
	}

	@Override
	public String computeValue() {
		return this.score;
	}

	public void method_12607(class_3915 arg) {
		MinecraftServer minecraftServer = arg.method_17473();
		if (minecraftServer != null && minecraftServer.hasGameDir() && ChatUtil.isEmpty(this.score)) {
			Scoreboard scoreboard = minecraftServer.method_20333();
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(this.objective);
			if (scoreboard.playerHasObjective(this.name, scoreboardObjective)) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(this.name, scoreboardObjective);
				this.setScore(String.format("%d", scoreboardPlayerScore.getScore()));
			} else {
				this.score = "";
			}
		}
	}

	public ScoreText copy() {
		ScoreText scoreText = new ScoreText(this.name, this.objective);
		scoreText.setScore(this.score);
		return scoreText;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof ScoreText)) {
			return false;
		} else {
			ScoreText scoreText = (ScoreText)object;
			return this.name.equals(scoreText.name) && this.objective.equals(scoreText.objective) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "ScoreComponent{name='"
			+ this.name
			+ '\''
			+ "objective='"
			+ this.objective
			+ '\''
			+ ", siblings="
			+ this.siblings
			+ ", style="
			+ this.getStyle()
			+ '}';
	}
}
