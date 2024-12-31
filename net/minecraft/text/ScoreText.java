package net.minecraft.text;

import net.minecraft.command.CommandSource;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatUtil;

public class ScoreText extends BaseText {
	private final String name;
	private final String objective;
	private String score = "";

	public ScoreText(String string, String string2) {
		this.name = string;
		this.objective = string2;
	}

	public String getName() {
		return this.name;
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

	public void method_12607(CommandSource commandSource) {
		MinecraftServer minecraftServer = commandSource.getMinecraftServer();
		if (minecraftServer != null && minecraftServer.hasGameDir() && ChatUtil.isEmpty(this.score)) {
			Scoreboard scoreboard = minecraftServer.getWorld(0).getScoreboard();
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
		scoreText.setStyle(this.getStyle().deepCopy());

		for (Text text : this.getSiblings()) {
			scoreText.append(text.copy());
		}

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
