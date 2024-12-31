package net.minecraft.world.level.storage;

import java.util.List;
import net.minecraft.client.ClientException;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.level.LevelProperties;

public interface LevelStorageAccess {
	String getFormat();

	SaveHandler createSaveHandler(String worldName, boolean createPlayerDataDir);

	List<LevelSummary> getLevelList() throws ClientException;

	void clearAll();

	LevelProperties getLevelProperties(String name);

	boolean isLevelNameValid(String name);

	boolean deleteLevel(String name);

	void renameLevel(String name, String newName);

	boolean isConvertible(String worldName);

	boolean needsConversion(String worldName);

	boolean convert(String worldName, ProgressListener progressListener);

	boolean levelExists(String name);
}
