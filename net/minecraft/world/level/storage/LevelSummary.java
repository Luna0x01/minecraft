package net.minecraft.world.level.storage;

import net.minecraft.world.level.LevelInfo;

public class LevelSummary implements Comparable<LevelSummary> {
	private final String fileName;
	private final String displayName;
	private final long lastPlayed;
	private final long sizeOnDisk;
	private final boolean requiresConversion;
	private final LevelInfo.GameMode gameMode;
	private final boolean hardcore;
	private final boolean cheats;

	public LevelSummary(String string, String string2, long l, long m, LevelInfo.GameMode gameMode, boolean bl, boolean bl2, boolean bl3) {
		this.fileName = string;
		this.displayName = string2;
		this.lastPlayed = l;
		this.sizeOnDisk = m;
		this.gameMode = gameMode;
		this.requiresConversion = bl;
		this.hardcore = bl2;
		this.cheats = bl3;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public long getSizeOnDisk() {
		return this.sizeOnDisk;
	}

	public boolean requiresConversion() {
		return this.requiresConversion;
	}

	public long getLastPlayed() {
		return this.lastPlayed;
	}

	public int compareTo(LevelSummary levelSummary) {
		if (this.lastPlayed < levelSummary.lastPlayed) {
			return 1;
		} else {
			return this.lastPlayed > levelSummary.lastPlayed ? -1 : this.fileName.compareTo(levelSummary.fileName);
		}
	}

	public LevelInfo.GameMode getGameMode() {
		return this.gameMode;
	}

	public boolean isHardcore() {
		return this.hardcore;
	}

	public boolean cheatsEnabled() {
		return this.cheats;
	}
}
