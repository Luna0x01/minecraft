package net.minecraft.scoreboard;

import java.util.Comparator;

public class ScoreboardPlayerScore {
	public static final Comparator<ScoreboardPlayerScore> field_5683 = new Comparator<ScoreboardPlayerScore>() {
		public int compare(ScoreboardPlayerScore scoreboardPlayerScore, ScoreboardPlayerScore scoreboardPlayerScore2) {
			if (scoreboardPlayerScore.getScore() > scoreboardPlayerScore2.getScore()) {
				return 1;
			} else {
				return scoreboardPlayerScore.getScore() < scoreboardPlayerScore2.getScore()
					? -1
					: scoreboardPlayerScore2.getPlayerName().compareToIgnoreCase(scoreboardPlayerScore.getPlayerName());
			}
		}
	};
	private final Scoreboard field_5684;
	private final ScoreboardObjective field_5685;
	private final String playerName;
	private int score;
	private boolean locked;
	private boolean forceUpdate;

	public ScoreboardPlayerScore(Scoreboard scoreboard, ScoreboardObjective scoreboardObjective, String string) {
		this.field_5684 = scoreboard;
		this.field_5685 = scoreboardObjective;
		this.playerName = string;
		this.forceUpdate = true;
	}

	public void incrementScore(int i) {
		if (this.field_5685.getCriterion().method_4919()) {
			throw new IllegalStateException("Cannot modify read-only score");
		} else {
			this.setScore(this.getScore() + i);
		}
	}

	public void method_4868(int i) {
		if (this.field_5685.getCriterion().method_4919()) {
			throw new IllegalStateException("Cannot modify read-only score");
		} else {
			this.setScore(this.getScore() - i);
		}
	}

	public void method_4865() {
		if (this.field_5685.getCriterion().method_4919()) {
			throw new IllegalStateException("Cannot modify read-only score");
		} else {
			this.incrementScore(1);
		}
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		int i = this.score;
		this.score = score;
		if (i != score || this.forceUpdate) {
			this.forceUpdate = false;
			this.getScoreboard().updateScore(this);
		}
	}

	public ScoreboardObjective getObjective() {
		return this.field_5685;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public Scoreboard getScoreboard() {
		return this.field_5684;
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
