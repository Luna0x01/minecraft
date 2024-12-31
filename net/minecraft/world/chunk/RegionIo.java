package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RegionIo {
	private static final Map<File, RegionFileFormat> FORMATS = Maps.newHashMap();

	public static synchronized RegionFileFormat create(File worldDir, int x, int y) {
		File file = new File(worldDir, "region");
		File file2 = new File(file, "r." + (x >> 5) + "." + (y >> 5) + ".mca");
		RegionFileFormat regionFileFormat = (RegionFileFormat)FORMATS.get(file2);
		if (regionFileFormat != null) {
			return regionFileFormat;
		} else {
			if (!file.exists()) {
				file.mkdirs();
			}

			if (FORMATS.size() >= 256) {
				clearRegionFormats();
			}

			RegionFileFormat regionFileFormat2 = new RegionFileFormat(file2);
			FORMATS.put(file2, regionFileFormat2);
			return regionFileFormat2;
		}
	}

	public static synchronized RegionFileFormat get(File worldDir, int x, int z) {
		File file = new File(worldDir, "region");
		File file2 = new File(file, "r." + (x >> 5) + "." + (z >> 5) + ".mca");
		RegionFileFormat regionFileFormat = (RegionFileFormat)FORMATS.get(file2);
		if (regionFileFormat != null) {
			return regionFileFormat;
		} else if (file.exists() && file2.exists()) {
			if (FORMATS.size() >= 256) {
				clearRegionFormats();
			}

			RegionFileFormat regionFileFormat2 = new RegionFileFormat(file2);
			FORMATS.put(file2, regionFileFormat2);
			return regionFileFormat2;
		} else {
			return null;
		}
	}

	public static synchronized void clearRegionFormats() {
		for (RegionFileFormat regionFileFormat : FORMATS.values()) {
			try {
				if (regionFileFormat != null) {
					regionFileFormat.close();
				}
			} catch (IOException var3) {
				var3.printStackTrace();
			}
		}

		FORMATS.clear();
	}

	public static DataInputStream read(File worldDir, int x, int y) {
		RegionFileFormat regionFileFormat = create(worldDir, x, y);
		return regionFileFormat.getChunkInputStream(x & 31, y & 31);
	}

	public static DataOutputStream write(File worldDir, int x, int y) {
		RegionFileFormat regionFileFormat = create(worldDir, x, y);
		return regionFileFormat.getChunkOutputStream(x & 31, y & 31);
	}

	public static boolean chunkExists(File worldDir, int x, int z) {
		RegionFileFormat regionFileFormat = get(worldDir, x, z);
		return regionFileFormat != null ? regionFileFormat.chunkExists(x & 31, z & 31) : false;
	}
}
