package net.minecraft.realms;

import net.minecraft.world.level.storage.LevelSummary;

public class RealmsLevelSummary implements Comparable<RealmsLevelSummary> {
	private LevelSummary levelSummary;

	public RealmsLevelSummary(LevelSummary levelSummary) {
		this.levelSummary = levelSummary;
	}

	public int getGameMode() {
		return this.levelSummary.getGameMode().getId();
	}

	public String getLevelId() {
		return this.levelSummary.getFileName();
	}

	public boolean hasCheats() {
		return this.levelSummary.cheatsEnabled();
	}

	public boolean isHardcore() {
		return this.levelSummary.isHardcore();
	}

	public boolean isRequiresConversion() {
		return this.levelSummary.requiresConversion();
	}

	public String getLevelName() {
		return this.levelSummary.getDisplayName();
	}

	public long getLastPlayed() {
		return this.levelSummary.getLastPlayed();
	}

	public int compareTo(LevelSummary levelSummary) {
		return this.levelSummary.compareTo(levelSummary);
	}

	public long getSizeOnDisk() {
		return this.levelSummary.getSizeOnDisk();
	}

	public int compareTo(RealmsLevelSummary realmsLevelSummary) {
		if (this.levelSummary.getLastPlayed() < realmsLevelSummary.getLastPlayed()) {
			return 1;
		} else {
			return this.levelSummary.getLastPlayed() > realmsLevelSummary.getLastPlayed()
				? -1
				: this.levelSummary.getFileName().compareTo(realmsLevelSummary.getLevelId());
		}
	}
}
