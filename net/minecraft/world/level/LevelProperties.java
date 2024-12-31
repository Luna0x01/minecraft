package net.minecraft.world.level;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_4372;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRuleManager;
import net.minecraft.world.dimension.DimensionType;

public class LevelProperties {
	private String field_13100;
	private int field_13101;
	private boolean field_13102;
	public static final Difficulty NORMAL_DIFFICULTY = Difficulty.NORMAL;
	private long seed;
	private LevelGeneratorType levelGeneratorType = LevelGeneratorType.DEFAULT;
	private NbtCompound field_19765 = new NbtCompound();
	@Nullable
	private String field_19766;
	private int spawnX;
	private int spawnY;
	private int spawnZ;
	private long time;
	private long timeOfDay;
	private long lastPlayed;
	private long sizeOnDisk;
	@Nullable
	private final DataFixer field_19767;
	private final int field_19768;
	private boolean field_19769;
	private NbtCompound playerNbt;
	private int dimension;
	private String levelName;
	private int version;
	private int clearWeatherTime;
	private boolean raining;
	private int rainTime;
	private boolean thundering;
	private int thunderTime;
	private GameMode gameMode;
	private boolean structures;
	private boolean hardcore;
	private boolean allowCommands;
	private boolean initialized;
	private Difficulty difficulty;
	private boolean difficultyLocked;
	private double borderCenterX;
	private double borderCenterZ;
	private double borderSize = 6.0E7;
	private long borderSizeLerpTime;
	private double borderSizeLerpTarget;
	private double borderSafeZone = 5.0;
	private double borderDamagePerBlock = 0.2;
	private int borderWarningBlocks = 5;
	private int borderWarningTime = 15;
	private final Set<String> field_19762 = Sets.newHashSet();
	private final Set<String> field_19763 = Sets.newLinkedHashSet();
	private final Map<DimensionType, NbtCompound> field_13099 = Maps.newIdentityHashMap();
	private NbtCompound field_19764;
	private final GameRuleManager gameRules = new GameRuleManager();

	protected LevelProperties() {
		this.field_19767 = null;
		this.field_19768 = 1631;
		this.method_17964(new NbtCompound());
	}

	public LevelProperties(NbtCompound nbtCompound, DataFixer dataFixer, int i, @Nullable NbtCompound nbtCompound2) {
		this.field_19767 = dataFixer;
		if (nbtCompound.contains("Version", 10)) {
			NbtCompound nbtCompound3 = nbtCompound.getCompound("Version");
			this.field_13100 = nbtCompound3.getString("Name");
			this.field_13101 = nbtCompound3.getInt("Id");
			this.field_13102 = nbtCompound3.getBoolean("Snapshot");
		}

		this.seed = nbtCompound.getLong("RandomSeed");
		if (nbtCompound.contains("generatorName", 8)) {
			String string = nbtCompound.getString("generatorName");
			this.levelGeneratorType = LevelGeneratorType.getTypeFromName(string);
			if (this.levelGeneratorType == null) {
				this.levelGeneratorType = LevelGeneratorType.DEFAULT;
			} else if (this.levelGeneratorType == LevelGeneratorType.CUSTOMIZED) {
				this.field_19766 = nbtCompound.getString("generatorOptions");
			} else if (this.levelGeneratorType.isVersioned()) {
				int j = 0;
				if (nbtCompound.contains("generatorVersion", 99)) {
					j = nbtCompound.getInt("generatorVersion");
				}

				this.levelGeneratorType = this.levelGeneratorType.getTypeForVersion(j);
			}

			this.method_17964(nbtCompound.getCompound("generatorOptions"));
		}

		this.gameMode = GameMode.setGameModeWithId(nbtCompound.getInt("GameType"));
		if (nbtCompound.contains("legacy_custom_options", 8)) {
			this.field_19766 = nbtCompound.getString("legacy_custom_options");
		}

		if (nbtCompound.contains("MapFeatures", 99)) {
			this.structures = nbtCompound.getBoolean("MapFeatures");
		} else {
			this.structures = true;
		}

		this.spawnX = nbtCompound.getInt("SpawnX");
		this.spawnY = nbtCompound.getInt("SpawnY");
		this.spawnZ = nbtCompound.getInt("SpawnZ");
		this.time = nbtCompound.getLong("Time");
		if (nbtCompound.contains("DayTime", 99)) {
			this.timeOfDay = nbtCompound.getLong("DayTime");
		} else {
			this.timeOfDay = this.time;
		}

		this.lastPlayed = nbtCompound.getLong("LastPlayed");
		this.sizeOnDisk = nbtCompound.getLong("SizeOnDisk");
		this.levelName = nbtCompound.getString("LevelName");
		this.version = nbtCompound.getInt("version");
		this.clearWeatherTime = nbtCompound.getInt("clearWeatherTime");
		this.rainTime = nbtCompound.getInt("rainTime");
		this.raining = nbtCompound.getBoolean("raining");
		this.thunderTime = nbtCompound.getInt("thunderTime");
		this.thundering = nbtCompound.getBoolean("thundering");
		this.hardcore = nbtCompound.getBoolean("hardcore");
		if (nbtCompound.contains("initialized", 99)) {
			this.initialized = nbtCompound.getBoolean("initialized");
		} else {
			this.initialized = true;
		}

		if (nbtCompound.contains("allowCommands", 99)) {
			this.allowCommands = nbtCompound.getBoolean("allowCommands");
		} else {
			this.allowCommands = this.gameMode == GameMode.CREATIVE;
		}

		this.field_19768 = i;
		if (nbtCompound2 != null) {
			this.playerNbt = nbtCompound2;
		}

		if (nbtCompound.contains("GameRules", 10)) {
			this.gameRules.setNbt(nbtCompound.getCompound("GameRules"));
		}

		if (nbtCompound.contains("Difficulty", 99)) {
			this.difficulty = Difficulty.byOrdinal(nbtCompound.getByte("Difficulty"));
		}

		if (nbtCompound.contains("DifficultyLocked", 1)) {
			this.difficultyLocked = nbtCompound.getBoolean("DifficultyLocked");
		}

		if (nbtCompound.contains("BorderCenterX", 99)) {
			this.borderCenterX = nbtCompound.getDouble("BorderCenterX");
		}

		if (nbtCompound.contains("BorderCenterZ", 99)) {
			this.borderCenterZ = nbtCompound.getDouble("BorderCenterZ");
		}

		if (nbtCompound.contains("BorderSize", 99)) {
			this.borderSize = nbtCompound.getDouble("BorderSize");
		}

		if (nbtCompound.contains("BorderSizeLerpTime", 99)) {
			this.borderSizeLerpTime = nbtCompound.getLong("BorderSizeLerpTime");
		}

		if (nbtCompound.contains("BorderSizeLerpTarget", 99)) {
			this.borderSizeLerpTarget = nbtCompound.getDouble("BorderSizeLerpTarget");
		}

		if (nbtCompound.contains("BorderSafeZone", 99)) {
			this.borderSafeZone = nbtCompound.getDouble("BorderSafeZone");
		}

		if (nbtCompound.contains("BorderDamagePerBlock", 99)) {
			this.borderDamagePerBlock = nbtCompound.getDouble("BorderDamagePerBlock");
		}

		if (nbtCompound.contains("BorderWarningBlocks", 99)) {
			this.borderWarningBlocks = nbtCompound.getInt("BorderWarningBlocks");
		}

		if (nbtCompound.contains("BorderWarningTime", 99)) {
			this.borderWarningTime = nbtCompound.getInt("BorderWarningTime");
		}

		if (nbtCompound.contains("DimensionData", 10)) {
			NbtCompound nbtCompound4 = nbtCompound.getCompound("DimensionData");

			for (String string2 : nbtCompound4.getKeys()) {
				this.field_13099.put(DimensionType.method_17195(Integer.parseInt(string2)), nbtCompound4.getCompound(string2));
			}
		}

		if (nbtCompound.contains("DataPacks", 10)) {
			NbtCompound nbtCompound5 = nbtCompound.getCompound("DataPacks");
			NbtList nbtList = nbtCompound5.getList("Disabled", 8);

			for (int k = 0; k < nbtList.size(); k++) {
				this.field_19762.add(nbtList.getString(k));
			}

			NbtList nbtList2 = nbtCompound5.getList("Enabled", 8);

			for (int l = 0; l < nbtList2.size(); l++) {
				this.field_19763.add(nbtList2.getString(l));
			}
		}

		if (nbtCompound.contains("CustomBossEvents", 10)) {
			this.field_19764 = nbtCompound.getCompound("CustomBossEvents");
		}
	}

	public LevelProperties(LevelInfo levelInfo, String string) {
		this.field_19767 = null;
		this.field_19768 = 1631;
		this.copyFrom(levelInfo);
		this.levelName = string;
		this.difficulty = NORMAL_DIFFICULTY;
		this.initialized = false;
	}

	public void copyFrom(LevelInfo info) {
		this.seed = info.getSeed();
		this.gameMode = info.method_3758();
		this.structures = info.hasStructures();
		this.hardcore = info.isHardcore();
		this.levelGeneratorType = info.getGeneratorType();
		this.method_17964((NbtCompound)Dynamic.convert(JsonOps.INSTANCE, class_4372.field_21487, info.method_4695()));
		this.allowCommands = info.allowCommands();
	}

	public NbtCompound toNbt(@Nullable NbtCompound nbt) {
		this.method_17954();
		if (nbt == null) {
			nbt = this.playerNbt;
		}

		NbtCompound nbtCompound = new NbtCompound();
		this.putNbt(nbtCompound, nbt);
		return nbtCompound;
	}

	private void putNbt(NbtCompound worldNbt, NbtCompound playerData) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("Name", "1.13.2");
		nbtCompound.putInt("Id", 1631);
		nbtCompound.putBoolean("Snapshot", false);
		worldNbt.put("Version", nbtCompound);
		worldNbt.putInt("DataVersion", 1631);
		worldNbt.putLong("RandomSeed", this.seed);
		worldNbt.putString("generatorName", this.levelGeneratorType.method_16401());
		worldNbt.putInt("generatorVersion", this.levelGeneratorType.getVersion());
		if (!this.field_19765.isEmpty()) {
			worldNbt.put("generatorOptions", this.field_19765);
		}

		if (this.field_19766 != null) {
			worldNbt.putString("legacy_custom_options", this.field_19766);
		}

		worldNbt.putInt("GameType", this.gameMode.getGameModeId());
		worldNbt.putBoolean("MapFeatures", this.structures);
		worldNbt.putInt("SpawnX", this.spawnX);
		worldNbt.putInt("SpawnY", this.spawnY);
		worldNbt.putInt("SpawnZ", this.spawnZ);
		worldNbt.putLong("Time", this.time);
		worldNbt.putLong("DayTime", this.timeOfDay);
		worldNbt.putLong("SizeOnDisk", this.sizeOnDisk);
		worldNbt.putLong("LastPlayed", Util.method_20231());
		worldNbt.putString("LevelName", this.levelName);
		worldNbt.putInt("version", this.version);
		worldNbt.putInt("clearWeatherTime", this.clearWeatherTime);
		worldNbt.putInt("rainTime", this.rainTime);
		worldNbt.putBoolean("raining", this.raining);
		worldNbt.putInt("thunderTime", this.thunderTime);
		worldNbt.putBoolean("thundering", this.thundering);
		worldNbt.putBoolean("hardcore", this.hardcore);
		worldNbt.putBoolean("allowCommands", this.allowCommands);
		worldNbt.putBoolean("initialized", this.initialized);
		worldNbt.putDouble("BorderCenterX", this.borderCenterX);
		worldNbt.putDouble("BorderCenterZ", this.borderCenterZ);
		worldNbt.putDouble("BorderSize", this.borderSize);
		worldNbt.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
		worldNbt.putDouble("BorderSafeZone", this.borderSafeZone);
		worldNbt.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
		worldNbt.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
		worldNbt.putDouble("BorderWarningBlocks", (double)this.borderWarningBlocks);
		worldNbt.putDouble("BorderWarningTime", (double)this.borderWarningTime);
		if (this.difficulty != null) {
			worldNbt.putByte("Difficulty", (byte)this.difficulty.getId());
		}

		worldNbt.putBoolean("DifficultyLocked", this.difficultyLocked);
		worldNbt.put("GameRules", this.gameRules.getNbt());
		NbtCompound nbtCompound2 = new NbtCompound();

		for (Entry<DimensionType, NbtCompound> entry : this.field_13099.entrySet()) {
			nbtCompound2.put(String.valueOf(((DimensionType)entry.getKey()).method_17201()), (NbtElement)entry.getValue());
		}

		worldNbt.put("DimensionData", nbtCompound2);
		if (playerData != null) {
			worldNbt.put("Player", playerData);
		}

		NbtCompound nbtCompound3 = new NbtCompound();
		NbtList nbtList = new NbtList();

		for (String string : this.field_19763) {
			nbtList.add((NbtElement)(new NbtString(string)));
		}

		nbtCompound3.put("Enabled", nbtList);
		NbtList nbtList2 = new NbtList();

		for (String string2 : this.field_19762) {
			nbtList2.add((NbtElement)(new NbtString(string2)));
		}

		nbtCompound3.put("Disabled", nbtList2);
		worldNbt.put("DataPacks", nbtCompound3);
		if (this.field_19764 != null) {
			worldNbt.put("CustomBossEvents", this.field_19764);
		}
	}

	public long getSeed() {
		return this.seed;
	}

	public int getSpawnX() {
		return this.spawnX;
	}

	public int getSpawnY() {
		return this.spawnY;
	}

	public int getSpawnZ() {
		return this.spawnZ;
	}

	public long getTime() {
		return this.time;
	}

	public long getTimeOfDay() {
		return this.timeOfDay;
	}

	public long getSizeOnDisk() {
		return this.sizeOnDisk;
	}

	private void method_17954() {
		if (!this.field_19769 && this.playerNbt != null) {
			if (this.field_19768 < 1631) {
				if (this.field_19767 == null) {
					throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
				}

				this.playerNbt = NbtHelper.method_20141(this.field_19767, DataFixTypes.PLAYER, this.playerNbt, this.field_19768);
			}

			this.dimension = this.playerNbt.getInt("Dimension");
			this.field_19769 = true;
		}
	}

	public NbtCompound getNbt() {
		this.method_17954();
		return this.playerNbt;
	}

	public int method_17966() {
		this.method_17954();
		return this.dimension;
	}

	public void setSpawnX(int spawnX) {
		this.spawnX = spawnX;
	}

	public void setSpawnY(int spawnY) {
		this.spawnY = spawnY;
	}

	public void setSpawnZ(int spawnZ) {
		this.spawnZ = spawnZ;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setDayTime(long time) {
		this.timeOfDay = time;
	}

	public void setSpawnPos(BlockPos pos) {
		this.spawnX = pos.getX();
		this.spawnY = pos.getY();
		this.spawnZ = pos.getZ();
	}

	public String getLevelName() {
		return this.levelName;
	}

	public void setLevelName(String name) {
		this.levelName = name;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getLastPlayed() {
		return this.lastPlayed;
	}

	public int getClearWeatherTime() {
		return this.clearWeatherTime;
	}

	public void setClearWeatherTime(int time) {
		this.clearWeatherTime = time;
	}

	public boolean isThundering() {
		return this.thundering;
	}

	public void setThundering(boolean thundering) {
		this.thundering = thundering;
	}

	public int getThunderTime() {
		return this.thunderTime;
	}

	public void setThunderTime(int time) {
		this.thunderTime = time;
	}

	public boolean isRaining() {
		return this.raining;
	}

	public void setRaining(boolean raining) {
		this.raining = raining;
	}

	public int getRainTime() {
		return this.rainTime;
	}

	public void setRainTime(int rainTime) {
		this.rainTime = rainTime;
	}

	public GameMode getGamemode() {
		return this.gameMode;
	}

	public boolean hasStructures() {
		return this.structures;
	}

	public void setStructures(boolean structures) {
		this.structures = structures;
	}

	public void getGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public boolean isHardcore() {
		return this.hardcore;
	}

	public void setHardcore(boolean hardcore) {
		this.hardcore = hardcore;
	}

	public LevelGeneratorType getGeneratorType() {
		return this.levelGeneratorType;
	}

	public void setLevelGeneratorType(LevelGeneratorType type) {
		this.levelGeneratorType = type;
	}

	public NbtCompound method_17950() {
		return this.field_19765;
	}

	public void method_17964(NbtCompound nbtCompound) {
		this.field_19765 = nbtCompound;
	}

	public boolean areCheatsEnabled() {
		return this.allowCommands;
	}

	public void setCheats(boolean enabled) {
		this.allowCommands = enabled;
	}

	public boolean isInitialized() {
		return this.initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public GameRuleManager getGamerules() {
		return this.gameRules;
	}

	public double getBorderCenterX() {
		return this.borderCenterX;
	}

	public double getBorderCenterZ() {
		return this.borderCenterZ;
	}

	public double getBorderSize() {
		return this.borderSize;
	}

	public void setBorderSize(double size) {
		this.borderSize = size;
	}

	public long getBorderSizeLerpTime() {
		return this.borderSizeLerpTime;
	}

	public void setBorderSizeLerpTime(long time) {
		this.borderSizeLerpTime = time;
	}

	public double getBorderSizeLerpTarget() {
		return this.borderSizeLerpTarget;
	}

	public void setBorderSizeLerpTarget(double target) {
		this.borderSizeLerpTarget = target;
	}

	public void setBorderCenterZ(double y) {
		this.borderCenterZ = y;
	}

	public void setBorderCenterX(double x) {
		this.borderCenterX = x;
	}

	public double getSafeZone() {
		return this.borderSafeZone;
	}

	public void setSafeZone(double zone) {
		this.borderSafeZone = zone;
	}

	public double getBorderDamagePerBlock() {
		return this.borderDamagePerBlock;
	}

	public void setBorderDamagePerBlock(double damage) {
		this.borderDamagePerBlock = damage;
	}

	public int getBorderWarningBlocks() {
		return this.borderWarningBlocks;
	}

	public int getBorderWarningTime() {
		return this.borderWarningTime;
	}

	public void setBorderWarningBlocks(int blocks) {
		this.borderWarningBlocks = blocks;
	}

	public void setBorderWarningTime(int time) {
		this.borderWarningTime = time;
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public boolean isDifficultyLocked() {
		return this.difficultyLocked;
	}

	public void setDifficultyLocked(boolean difficultyLocked) {
		this.difficultyLocked = difficultyLocked;
	}

	public void addToCrashReport(CrashReportSection section) {
		section.add("Level seed", (CrashCallable<String>)(() -> String.valueOf(this.getSeed())));
		section.add(
			"Level generator",
			(CrashCallable<String>)(() -> String.format(
					"ID %02d - %s, ver %d. Features enabled: %b",
					this.levelGeneratorType.getId(),
					this.levelGeneratorType.getName(),
					this.levelGeneratorType.getVersion(),
					this.structures
				))
		);
		section.add("Level generator options", (CrashCallable<String>)(() -> this.field_19765.toString()));
		section.add("Level spawn location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this.spawnX, this.spawnY, this.spawnZ)));
		section.add("Level time", (CrashCallable<String>)(() -> String.format("%d game time, %d day time", this.time, this.timeOfDay)));
		section.add("Level dimension", (CrashCallable<String>)(() -> String.valueOf(this.dimension)));
		section.add("Level storage version", (CrashCallable<String>)(() -> {
			String string = "Unknown?";

			try {
				switch (this.version) {
					case 19132:
						string = "McRegion";
						break;
					case 19133:
						string = "Anvil";
				}
			} catch (Throwable var3) {
			}

			return String.format("0x%05X - %s", this.version, string);
		}));
		section.add(
			"Level weather",
			(CrashCallable<String>)(() -> String.format(
					"Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering
				))
		);
		section.add(
			"Level game mode",
			(CrashCallable<String>)(() -> String.format(
					"Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.gameMode.getGameModeName(), this.gameMode.getGameModeId(), this.hardcore, this.allowCommands
				))
		);
	}

	public NbtCompound method_11954(DimensionType dimensionType) {
		NbtCompound nbtCompound = (NbtCompound)this.field_13099.get(dimensionType);
		return nbtCompound == null ? new NbtCompound() : nbtCompound;
	}

	public void method_11955(DimensionType dimensionType, NbtCompound nbtCompound) {
		this.field_13099.put(dimensionType, nbtCompound);
	}

	public int method_11951() {
		return this.field_13101;
	}

	public boolean method_11952() {
		return this.field_13102;
	}

	public String method_11953() {
		return this.field_13100;
	}

	public Set<String> method_17951() {
		return this.field_19762;
	}

	public Set<String> method_17952() {
		return this.field_19763;
	}

	@Nullable
	public NbtCompound method_17953() {
		return this.field_19764;
	}

	public void method_17965(@Nullable NbtCompound nbtCompound) {
		this.field_19764 = nbtCompound;
	}
}
