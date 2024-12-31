package net.minecraft.world.level;

import net.minecraft.entity.player.PlayerAbilities;

public final class LevelInfo {
	private final long seed;
	private final LevelInfo.GameMode gameMode;
	private final boolean structures;
	private final boolean hardcore;
	private final LevelGeneratorType generatorType;
	private boolean commands;
	private boolean bonusChest;
	private String generatorOptions = "";

	public LevelInfo(long l, LevelInfo.GameMode gameMode, boolean bl, boolean bl2, LevelGeneratorType levelGeneratorType) {
		this.seed = l;
		this.gameMode = gameMode;
		this.structures = bl;
		this.hardcore = bl2;
		this.generatorType = levelGeneratorType;
	}

	public LevelInfo(LevelProperties levelProperties) {
		this(
			levelProperties.getSeed(), levelProperties.getGameMode(), levelProperties.hasStructures(), levelProperties.isHardcore(), levelProperties.getGeneratorType()
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

	public LevelInfo setGeneratorOptions(String generatorOptions) {
		this.generatorOptions = generatorOptions;
		return this;
	}

	public boolean hasBonusChest() {
		return this.bonusChest;
	}

	public long getSeed() {
		return this.seed;
	}

	public LevelInfo.GameMode getGameMode() {
		return this.gameMode;
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

	public static LevelInfo.GameMode getGameModeById(int id) {
		return LevelInfo.GameMode.byId(id);
	}

	public String getGeneratorOptions() {
		return this.generatorOptions;
	}

	public static enum GameMode {
		NOT_SET(-1, ""),
		SURVIVAL(0, "survival"),
		CREATIVE(1, "creative"),
		ADVENTURE(2, "adventure"),
		SPECTATOR(3, "spectator");

		int id;
		String name;

		private GameMode(int j, String string2) {
			this.id = j;
			this.name = string2;
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public void setAbilities(PlayerAbilities abilities) {
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

			abilities.allowModifyWorld = !this.shouldLimitWorldModification();
		}

		public boolean shouldLimitWorldModification() {
			return this == ADVENTURE || this == SPECTATOR;
		}

		public boolean isCreative() {
			return this == CREATIVE;
		}

		public boolean isSurvivalLike() {
			return this == SURVIVAL || this == ADVENTURE;
		}

		public static LevelInfo.GameMode byId(int id) {
			for (LevelInfo.GameMode gameMode : values()) {
				if (gameMode.id == id) {
					return gameMode;
				}
			}

			return SURVIVAL;
		}

		public static LevelInfo.GameMode byName(String name) {
			for (LevelInfo.GameMode gameMode : values()) {
				if (gameMode.name.equals(name)) {
					return gameMode;
				}
			}

			return SURVIVAL;
		}
	}
}
