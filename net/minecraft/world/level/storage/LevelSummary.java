package net.minecraft.world.level.storage;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;

public class LevelSummary implements Comparable<LevelSummary> {
	private final String fileName;
	private final String displayName;
	private final long lastPlayed;
	private final long sizeOnDisk;
	private final boolean requiresConversion;
	private final GameMode field_259;
	private final boolean hardcore;
	private final boolean cheats;
	private final String field_13103;
	private final int field_13104;
	private final boolean field_13105;
	private final LevelGeneratorType field_19775;

	public LevelSummary(LevelProperties levelProperties, String string, String string2, long l, boolean bl) {
		this.fileName = string;
		this.displayName = string2;
		this.lastPlayed = levelProperties.getLastPlayed();
		this.sizeOnDisk = l;
		this.field_259 = levelProperties.getGamemode();
		this.requiresConversion = bl;
		this.hardcore = levelProperties.isHardcore();
		this.cheats = levelProperties.areCheatsEnabled();
		this.field_13103 = levelProperties.method_11953();
		this.field_13104 = levelProperties.method_11951();
		this.field_13105 = levelProperties.method_11952();
		this.field_19775 = levelProperties.getGeneratorType();
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

	public GameMode method_261() {
		return this.field_259;
	}

	public boolean isHardcore() {
		return this.hardcore;
	}

	public boolean cheatsEnabled() {
		return this.cheats;
	}

	public Text method_17972() {
		return (Text)(ChatUtil.isEmpty(this.field_13103) ? new TranslatableText("selectWorld.versionUnknown") : new LiteralText(this.field_13103));
	}

	public boolean method_11959() {
		return this.method_11960() || this.method_17974() || this.method_17973();
	}

	public boolean method_11960() {
		return this.field_13104 > 1631;
	}

	public boolean method_17973() {
		return this.field_19775 == LevelGeneratorType.CUSTOMIZED && this.field_13104 < 1466;
	}

	public boolean method_17974() {
		return this.field_13104 < 1631;
	}
}
