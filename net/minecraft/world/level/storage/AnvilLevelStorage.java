package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.ClientException;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.AnvilWorldSaveHandler;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.class_2711;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.chunk.RegionFileFormat;
import net.minecraft.world.chunk.RegionIo;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilLevelStorage extends LevelStorage {
	private static final Logger LOGGER = LogManager.getLogger();

	public AnvilLevelStorage(File file, DataFixerUpper dataFixerUpper) {
		super(file, dataFixerUpper);
	}

	@Override
	public String getFormat() {
		return "Anvil";
	}

	@Override
	public List<LevelSummary> getLevelList() throws ClientException {
		if (this.file != null && this.file.exists() && this.file.isDirectory()) {
			List<LevelSummary> list = Lists.newArrayList();
			File[] files = this.file.listFiles();

			for (File file : files) {
				if (file.isDirectory()) {
					String string = file.getName();
					LevelProperties levelProperties = this.getLevelProperties(string);
					if (levelProperties != null && (levelProperties.getVersion() == 19132 || levelProperties.getVersion() == 19133)) {
						boolean bl = levelProperties.getVersion() != this.getCurrentVersion();
						String string2 = levelProperties.getLevelName();
						if (StringUtils.isEmpty(string2)) {
							string2 = string;
						}

						long l = 0L;
						list.add(new LevelSummary(levelProperties, string, string2, l, bl));
					}
				}
			}

			return list;
		} else {
			throw new ClientException("Unable to read or access folder where game worlds are saved!");
		}
	}

	protected int getCurrentVersion() {
		return 19133;
	}

	@Override
	public void clearAll() {
		RegionIo.clearRegionFormats();
	}

	@Override
	public SaveHandler createSaveHandler(String worldName, boolean createPlayerDataDir) {
		return new AnvilWorldSaveHandler(this.file, worldName, createPlayerDataDir, this.field_13098);
	}

	@Override
	public boolean isConvertible(String worldName) {
		LevelProperties levelProperties = this.getLevelProperties(worldName);
		return levelProperties != null && levelProperties.getVersion() == 19132;
	}

	@Override
	public boolean needsConversion(String worldName) {
		LevelProperties levelProperties = this.getLevelProperties(worldName);
		return levelProperties != null && levelProperties.getVersion() != this.getCurrentVersion();
	}

	@Override
	public boolean convert(String worldName, ProgressListener progressListener) {
		progressListener.setProgressPercentage(0);
		List<File> list = Lists.newArrayList();
		List<File> list2 = Lists.newArrayList();
		List<File> list3 = Lists.newArrayList();
		File file = new File(this.file, worldName);
		File file2 = new File(file, "DIM-1");
		File file3 = new File(file, "DIM1");
		LOGGER.info("Scanning folders...");
		this.addRegionFiles(file, list);
		if (file2.exists()) {
			this.addRegionFiles(file2, list2);
		}

		if (file3.exists()) {
			this.addRegionFiles(file3, list3);
		}

		int i = list.size() + list2.size() + list3.size();
		LOGGER.info("Total conversion count is " + i);
		LevelProperties levelProperties = this.getLevelProperties(worldName);
		SingletonBiomeSource singletonBiomeSource = null;
		if (levelProperties.getGeneratorType() == LevelGeneratorType.FLAT) {
			singletonBiomeSource = new class_2711(Biomes.PLAINS);
		} else {
			singletonBiomeSource = new SingletonBiomeSource(levelProperties);
		}

		this.method_193(new File(file, "region"), list, singletonBiomeSource, 0, i, progressListener);
		this.method_193(new File(file2, "region"), list2, new class_2711(Biomes.NETHER), list.size(), i, progressListener);
		this.method_193(new File(file3, "region"), list3, new class_2711(Biomes.SKY), list.size() + list2.size(), i, progressListener);
		levelProperties.setVersion(19133);
		if (levelProperties.getGeneratorType() == LevelGeneratorType.DEFAULT_1_1) {
			levelProperties.setLevelGeneratorType(LevelGeneratorType.DEFAULT);
		}

		this.makeMcrLevelDatBackup(worldName);
		SaveHandler saveHandler = this.createSaveHandler(worldName, false);
		saveHandler.saveWorld(levelProperties);
		return true;
	}

	private void makeMcrLevelDatBackup(String worldName) {
		File file = new File(this.file, worldName);
		if (!file.exists()) {
			LOGGER.warn("Unable to create level.dat_mcr backup");
		} else {
			File file2 = new File(file, "level.dat");
			if (!file2.exists()) {
				LOGGER.warn("Unable to create level.dat_mcr backup");
			} else {
				File file3 = new File(file, "level.dat_mcr");
				if (!file2.renameTo(file3)) {
					LOGGER.warn("Unable to create level.dat_mcr backup");
				}
			}
		}
	}

	private void method_193(File file, Iterable<File> iterable, SingletonBiomeSource singletonBiomeSource, int i, int j, ProgressListener progressListener) {
		for (File file2 : iterable) {
			this.method_192(file, file2, singletonBiomeSource, i, j, progressListener);
			i++;
			int k = (int)Math.round(100.0 * (double)i / (double)j);
			progressListener.setProgressPercentage(k);
		}
	}

	private void method_192(File file, File file2, SingletonBiomeSource singletonBiomeSource, int i, int j, ProgressListener progressListener) {
		try {
			String string = file2.getName();
			RegionFileFormat regionFileFormat = new RegionFileFormat(file2);
			RegionFileFormat regionFileFormat2 = new RegionFileFormat(new File(file, string.substring(0, string.length() - ".mcr".length()) + ".mca"));

			for (int k = 0; k < 32; k++) {
				for (int l = 0; l < 32; l++) {
					if (regionFileFormat.chunkExists(k, l) && !regionFileFormat2.chunkExists(k, l)) {
						DataInputStream dataInputStream = regionFileFormat.getChunkInputStream(k, l);
						if (dataInputStream == null) {
							LOGGER.warn("Failed to fetch input stream");
						} else {
							NbtCompound nbtCompound = NbtIo.read(dataInputStream);
							dataInputStream.close();
							NbtCompound nbtCompound2 = nbtCompound.getCompound("Level");
							AlphaChunkIo.AlphaChunk alphaChunk = AlphaChunkIo.readAlphaChunk(nbtCompound2);
							NbtCompound nbtCompound3 = new NbtCompound();
							NbtCompound nbtCompound4 = new NbtCompound();
							nbtCompound3.put("Level", nbtCompound4);
							AlphaChunkIo.method_3956(alphaChunk, nbtCompound4, singletonBiomeSource);
							DataOutputStream dataOutputStream = regionFileFormat2.getChunkOutputStream(k, l);
							NbtIo.write(nbtCompound3, dataOutputStream);
							dataOutputStream.close();
						}
					}
				}

				int m = (int)Math.round(100.0 * (double)(i * 1024) / (double)(j * 1024));
				int n = (int)Math.round(100.0 * (double)((k + 1) * 32 + i * 1024) / (double)(j * 1024));
				if (n > m) {
					progressListener.setProgressPercentage(n);
				}
			}

			regionFileFormat.close();
			regionFileFormat2.close();
		} catch (IOException var19) {
			var19.printStackTrace();
		}
	}

	private void addRegionFiles(File worldDirectory, Collection<File> files) {
		File file = new File(worldDirectory, "region");
		File[] files2 = file.listFiles(new FilenameFilter() {
			public boolean accept(File file, String string) {
				return string.endsWith(".mcr");
			}
		});
		if (files2 != null) {
			Collections.addAll(files, files2);
		}
	}
}
