package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.RegionFileFormat;
import net.minecraft.world.dimension.DimensionType;

public class class_3456 {
	private static final Pattern field_16652 = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
	private final File field_16653;
	private final Map<DimensionType, List<ChunkPos>> field_16654;

	public class_3456(File file) {
		this.field_16653 = file;
		Builder<DimensionType, List<ChunkPos>> builder = ImmutableMap.builder();

		for (DimensionType dimensionType : DimensionType.method_17200()) {
			builder.put(dimensionType, this.method_15517(dimensionType));
		}

		this.field_16654 = builder.build();
	}

	private List<ChunkPos> method_15517(DimensionType dimensionType) {
		ArrayList<ChunkPos> arrayList = Lists.newArrayList();
		File file = dimensionType.method_17197(this.field_16653);
		List<File> list = this.method_15518(file);

		for (File file2 : list) {
			arrayList.addAll(this.method_15515(file2));
		}

		list.sort(File::compareTo);
		return arrayList;
	}

	private List<ChunkPos> method_15515(File file) {
		List<ChunkPos> list = Lists.newArrayList();
		RegionFileFormat regionFileFormat = null;

		try {
			Matcher matcher = field_16652.matcher(file.getName());
			if (!matcher.matches()) {
				return list;
			}

			int i = Integer.parseInt(matcher.group(1)) << 5;
			int j = Integer.parseInt(matcher.group(2)) << 5;
			regionFileFormat = new RegionFileFormat(file);

			for (int k = 0; k < 32; k++) {
				for (int l = 0; l < 32; l++) {
					if (regionFileFormat.method_17188(k, l)) {
						list.add(new ChunkPos(k + i, l + j));
					}
				}
			}
		} catch (Throwable var18) {
			return Lists.newArrayList();
		} finally {
			if (regionFileFormat != null) {
				try {
					regionFileFormat.close();
				} catch (IOException var17) {
				}
			}
		}

		return list;
	}

	private List<File> method_15518(File file) {
		File file2 = new File(file, "region");
		File[] files = file2.listFiles((filex, string) -> string.endsWith(".mca"));
		return files != null ? Lists.newArrayList(files) : Lists.newArrayList();
	}

	public List<ChunkPos> method_15514(DimensionType dimensionType) {
		return (List<ChunkPos>)this.field_16654.get(dimensionType);
	}
}
