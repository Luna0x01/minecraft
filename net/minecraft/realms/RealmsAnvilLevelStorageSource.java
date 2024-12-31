package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.ClientException;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.storage.LevelStorageAccess;
import net.minecraft.world.level.storage.LevelSummary;

public class RealmsAnvilLevelStorageSource {
	private LevelStorageAccess levelStorageSource;

	public RealmsAnvilLevelStorageSource(LevelStorageAccess levelStorageAccess) {
		this.levelStorageSource = levelStorageAccess;
	}

	public String getName() {
		return this.levelStorageSource.getFormat();
	}

	public boolean levelExists(String name) {
		return this.levelStorageSource.levelExists(name);
	}

	public boolean convertLevel(String worldName, ProgressListener progressListener) {
		return this.levelStorageSource.convert(worldName, progressListener);
	}

	public boolean requiresConversion(String worldName) {
		return this.levelStorageSource.needsConversion(worldName);
	}

	public boolean isNewLevelIdAcceptable(String name) {
		return this.levelStorageSource.isLevelNameValid(name);
	}

	public boolean deleteLevel(String name) {
		return this.levelStorageSource.deleteLevel(name);
	}

	public boolean isConvertible(String worldName) {
		return this.levelStorageSource.isConvertible(worldName);
	}

	public void renameLevel(String oldName, String newName) {
		this.levelStorageSource.renameLevel(oldName, newName);
	}

	public void clearAll() {
		this.levelStorageSource.clearAll();
	}

	public List<RealmsLevelSummary> getLevelList() throws ClientException {
		List<RealmsLevelSummary> list = Lists.newArrayList();

		for (LevelSummary levelSummary : this.levelStorageSource.getLevelList()) {
			list.add(new RealmsLevelSummary(levelSummary));
		}

		return list;
	}
}
