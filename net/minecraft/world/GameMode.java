package net.minecraft.world;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum GameMode {
	NOT_SET(-1, ""),
	SURVIVAL(0, "survival"),
	CREATIVE(1, "creative"),
	ADVENTURE(2, "adventure"),
	SPECTATOR(3, "spectator");

	private final int gameModeId;
	private final String gameModeName;

	public static GameMode[] gameModes() {
		return (GameMode[])field_4578.clone();
	}

	private GameMode(int j, String string2) {
		this.gameModeId = j;
		this.gameModeName = string2;
	}

	public int getGameModeId() {
		return this.gameModeId;
	}

	public String getGameModeName() {
		return this.gameModeName;
	}

	public Text method_16311() {
		return new TranslatableText("gameMode." + this.gameModeName);
	}

	public void gameModeAbilities(PlayerAbilities abilities) {
		if (this == CREATIVE) {
			abilities.allowFlying = true;
			abilities.creativeMode = true;
			abilities.invulnerable = true;
		} else if (this == SPECTATOR) {
			abilities.allowFlying = true;
			abilities.creativeMode = false;
			abilities.invulnerable = true;
			abilities.flying = true;
		} else {
			abilities.allowFlying = false;
			abilities.creativeMode = false;
			abilities.invulnerable = false;
			abilities.flying = false;
		}

		abilities.allowModifyWorld = !this.isAdventure();
	}

	public boolean isAdventure() {
		return this == ADVENTURE || this == SPECTATOR;
	}

	public boolean isCreative() {
		return this == CREATIVE;
	}

	public boolean canBeDamaged() {
		return this == SURVIVAL || this == ADVENTURE;
	}

	public static GameMode setGameModeWithId(int gamemode) {
		return method_11494(gamemode, SURVIVAL);
	}

	public static GameMode method_11494(int i, GameMode gameMode) {
		for (GameMode gameMode2 : gameModes()) {
			if (gameMode2.gameModeId == i) {
				return gameMode2;
			}
		}

		return gameMode;
	}

	public static GameMode setGameModeWithString(String gamemode) {
		return method_11495(gamemode, SURVIVAL);
	}

	public static GameMode method_11495(String string, GameMode gameMode) {
		for (GameMode gameMode2 : gameModes()) {
			if (gameMode2.gameModeName.equals(string)) {
				return gameMode2;
			}
		}

		return gameMode;
	}
}
