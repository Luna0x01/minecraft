package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRuleManager;
import net.minecraft.world.dimension.DimensionType;

public class ReadOnlyLevelProperties extends LevelProperties {
	private final LevelProperties levelProperties;

	public ReadOnlyLevelProperties(LevelProperties levelProperties) {
		this.levelProperties = levelProperties;
	}

	@Override
	public NbtCompound toNbt(@Nullable NbtCompound nbt) {
		return this.levelProperties.toNbt(nbt);
	}

	@Override
	public long getSeed() {
		return this.levelProperties.getSeed();
	}

	@Override
	public int getSpawnX() {
		return this.levelProperties.getSpawnX();
	}

	@Override
	public int getSpawnY() {
		return this.levelProperties.getSpawnY();
	}

	@Override
	public int getSpawnZ() {
		return this.levelProperties.getSpawnZ();
	}

	@Override
	public long getTime() {
		return this.levelProperties.getTime();
	}

	@Override
	public long getTimeOfDay() {
		return this.levelProperties.getTimeOfDay();
	}

	@Override
	public long getSizeOnDisk() {
		return this.levelProperties.getSizeOnDisk();
	}

	@Override
	public NbtCompound getNbt() {
		return this.levelProperties.getNbt();
	}

	@Override
	public String getLevelName() {
		return this.levelProperties.getLevelName();
	}

	@Override
	public int getVersion() {
		return this.levelProperties.getVersion();
	}

	@Override
	public long getLastPlayed() {
		return this.levelProperties.getLastPlayed();
	}

	@Override
	public boolean isThundering() {
		return this.levelProperties.isThundering();
	}

	@Override
	public int getThunderTime() {
		return this.levelProperties.getThunderTime();
	}

	@Override
	public boolean isRaining() {
		return this.levelProperties.isRaining();
	}

	@Override
	public int getRainTime() {
		return this.levelProperties.getRainTime();
	}

	@Override
	public GameMode getGamemode() {
		return this.levelProperties.getGamemode();
	}

	@Override
	public void setSpawnX(int spawnX) {
	}

	@Override
	public void setSpawnY(int spawnY) {
	}

	@Override
	public void setSpawnZ(int spawnZ) {
	}

	@Override
	public void setTime(long time) {
	}

	@Override
	public void setDayTime(long time) {
	}

	@Override
	public void setSpawnPos(BlockPos pos) {
	}

	@Override
	public void setLevelName(String name) {
	}

	@Override
	public void setVersion(int version) {
	}

	@Override
	public void setThundering(boolean thundering) {
	}

	@Override
	public void setThunderTime(int time) {
	}

	@Override
	public void setRaining(boolean raining) {
	}

	@Override
	public void setRainTime(int rainTime) {
	}

	@Override
	public boolean hasStructures() {
		return this.levelProperties.hasStructures();
	}

	@Override
	public boolean isHardcore() {
		return this.levelProperties.isHardcore();
	}

	@Override
	public LevelGeneratorType getGeneratorType() {
		return this.levelProperties.getGeneratorType();
	}

	@Override
	public void setLevelGeneratorType(LevelGeneratorType type) {
	}

	@Override
	public boolean areCheatsEnabled() {
		return this.levelProperties.areCheatsEnabled();
	}

	@Override
	public void setCheats(boolean enabled) {
	}

	@Override
	public boolean isInitialized() {
		return this.levelProperties.isInitialized();
	}

	@Override
	public void setInitialized(boolean initialized) {
	}

	@Override
	public GameRuleManager getGamerules() {
		return this.levelProperties.getGamerules();
	}

	@Override
	public Difficulty getDifficulty() {
		return this.levelProperties.getDifficulty();
	}

	@Override
	public void setDifficulty(Difficulty difficulty) {
	}

	@Override
	public boolean isDifficultyLocked() {
		return this.levelProperties.isDifficultyLocked();
	}

	@Override
	public void setDifficultyLocked(boolean difficultyLocked) {
	}

	@Override
	public void method_11955(DimensionType dimensionType, NbtCompound nbtCompound) {
		this.levelProperties.method_11955(dimensionType, nbtCompound);
	}

	@Override
	public NbtCompound method_11954(DimensionType dimensionType) {
		return this.levelProperties.method_11954(dimensionType);
	}
}
