package net.minecraft.world.level;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.GameMode;

public final class LevelInfo {
	private final long seed;
	private final GameMode field_4566;
	private final boolean structures;
	private final boolean hardcore;
	private final LevelGeneratorType generatorType;
	private boolean commands;
	private boolean bonusChest;
	private JsonElement field_17504 = new JsonObject();

	public LevelInfo(long l, GameMode gameMode, boolean bl, boolean bl2, LevelGeneratorType levelGeneratorType) {
		this.seed = l;
		this.field_4566 = gameMode;
		this.structures = bl;
		this.hardcore = bl2;
		this.generatorType = levelGeneratorType;
	}

	public LevelInfo(LevelProperties levelProperties) {
		this(
			levelProperties.getSeed(), levelProperties.getGamemode(), levelProperties.hasStructures(), levelProperties.isHardcore(), levelProperties.getGeneratorType()
		);
	}

	public LevelInfo setBonusChest() {
		this.bonusChest = true;
		return this;
	}

	public LevelInfo enableCommands() {
		this.commands = true;
		return this;
	}

	public LevelInfo method_16395(JsonElement jsonElement) {
		this.field_17504 = jsonElement;
		return this;
	}

	public boolean hasBonusChest() {
		return this.bonusChest;
	}

	public long getSeed() {
		return this.seed;
	}

	public GameMode method_3758() {
		return this.field_4566;
	}

	public boolean isHardcore() {
		return this.hardcore;
	}

	public boolean hasStructures() {
		return this.structures;
	}

	public LevelGeneratorType getGeneratorType() {
		return this.generatorType;
	}

	public boolean allowCommands() {
		return this.commands;
	}

	public static GameMode method_3754(int i) {
		return GameMode.setGameModeWithId(i);
	}

	public JsonElement method_4695() {
		return this.field_17504;
	}
}
