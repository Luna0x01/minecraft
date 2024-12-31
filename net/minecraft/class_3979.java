package net.minecraft;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3979 {
	private static final Logger field_19361 = LogManager.getLogger();
	private static final Map<String, String> field_19362 = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put("Village", "Village");
		hashMap.put("Mineshaft", "Mineshaft");
		hashMap.put("Mansion", "Mansion");
		hashMap.put("Igloo", "Temple");
		hashMap.put("Desert_Pyramid", "Temple");
		hashMap.put("Jungle_Pyramid", "Temple");
		hashMap.put("Swamp_Hut", "Temple");
		hashMap.put("Stronghold", "Stronghold");
		hashMap.put("Monument", "Monument");
		hashMap.put("Fortress", "Fortress");
		hashMap.put("EndCity", "EndCity");
	});
	private static final Map<String, String> field_19363 = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put("Iglu", "Igloo");
		hashMap.put("TeDP", "Desert_Pyramid");
		hashMap.put("TeJP", "Jungle_Pyramid");
		hashMap.put("TeSH", "Swamp_Hut");
	});
	private final boolean field_19364;
	private final Map<String, Long2ObjectMap<NbtCompound>> field_19365 = Maps.newHashMap();
	private final Map<String, class_3990> field_19366 = Maps.newHashMap();

	public class_3979(@Nullable class_4070 arg) {
		this.method_17613(arg);
		boolean bl = false;

		for (String string : this.method_17618()) {
			bl |= this.field_19365.get(string) != null;
		}

		this.field_19364 = bl;
	}

	public void method_17611(long l) {
		for (String string : this.method_17608()) {
			class_3990 lv = (class_3990)this.field_19366.get(string);
			if (lv != null && lv.method_17645(l)) {
				lv.method_17646(l);
				lv.markDirty();
			}
		}
	}

	public NbtCompound method_17614(NbtCompound nbtCompound) {
		NbtCompound nbtCompound2 = nbtCompound.getCompound("Level");
		ChunkPos chunkPos = new ChunkPos(nbtCompound2.getInt("xPos"), nbtCompound2.getInt("zPos"));
		if (this.method_17609(chunkPos.x, chunkPos.z)) {
			nbtCompound = this.method_17615(nbtCompound, chunkPos);
		}

		NbtCompound nbtCompound3 = nbtCompound2.getCompound("Structures");
		NbtCompound nbtCompound4 = nbtCompound3.getCompound("References");

		for (String string : this.method_17618()) {
			class_3902<?> lv = (class_3902<?>)class_3844.field_19152.get(string.toLowerCase(Locale.ROOT));
			if (!nbtCompound4.contains(string, 12) && lv != null) {
				int i = lv.method_17433();
				LongList longList = new LongArrayList();

				for (int j = chunkPos.x - i; j <= chunkPos.x + i; j++) {
					for (int k = chunkPos.z - i; k <= chunkPos.z + i; k++) {
						if (this.method_17610(j, k, string)) {
							longList.add(ChunkPos.getIdFromCoords(j, k));
						}
					}
				}

				nbtCompound4.putLongArray(string, longList);
			}
		}

		nbtCompound3.put("References", nbtCompound4);
		nbtCompound2.put("Structures", nbtCompound3);
		nbtCompound.put("Level", nbtCompound2);
		return nbtCompound;
	}

	protected abstract String[] method_17608();

	protected abstract String[] method_17618();

	private boolean method_17610(int i, int j, String string) {
		return !this.field_19364
			? false
			: this.field_19365.get(string) != null && ((class_3990)this.field_19366.get(field_19362.get(string))).method_17644(ChunkPos.getIdFromCoords(i, j));
	}

	private boolean method_17609(int i, int j) {
		if (!this.field_19364) {
			return false;
		} else {
			for (String string : this.method_17618()) {
				if (this.field_19365.get(string) != null && ((class_3990)this.field_19366.get(field_19362.get(string))).method_17645(ChunkPos.getIdFromCoords(i, j))) {
					return true;
				}
			}

			return false;
		}
	}

	private NbtCompound method_17615(NbtCompound nbtCompound, ChunkPos chunkPos) {
		NbtCompound nbtCompound2 = nbtCompound.getCompound("Level");
		NbtCompound nbtCompound3 = nbtCompound2.getCompound("Structures");
		NbtCompound nbtCompound4 = nbtCompound3.getCompound("Starts");

		for (String string : this.method_17618()) {
			Long2ObjectMap<NbtCompound> long2ObjectMap = (Long2ObjectMap<NbtCompound>)this.field_19365.get(string);
			if (long2ObjectMap != null) {
				long l = chunkPos.method_16281();
				if (((class_3990)this.field_19366.get(field_19362.get(string))).method_17645(l)) {
					NbtCompound nbtCompound5 = (NbtCompound)long2ObjectMap.get(l);
					if (nbtCompound5 != null) {
						nbtCompound4.put(string, nbtCompound5);
					}
				}
			}
		}

		nbtCompound3.put("Starts", nbtCompound4);
		nbtCompound2.put("Structures", nbtCompound3);
		nbtCompound.put("Level", nbtCompound2);
		return nbtCompound;
	}

	private void method_17613(@Nullable class_4070 arg) {
		if (arg != null) {
			for (String string : this.method_17608()) {
				NbtCompound nbtCompound = new NbtCompound();

				try {
					nbtCompound = arg.method_17978(string, 1493).getCompound("data").getCompound("Features");
					if (nbtCompound.isEmpty()) {
						continue;
					}
				} catch (IOException var15) {
				}

				for (String string2 : nbtCompound.getKeys()) {
					NbtCompound nbtCompound2 = nbtCompound.getCompound(string2);
					long l = ChunkPos.getIdFromCoords(nbtCompound2.getInt("ChunkX"), nbtCompound2.getInt("ChunkZ"));
					NbtList nbtList = nbtCompound2.getList("Children", 10);
					if (!nbtList.isEmpty()) {
						String string3 = nbtList.getCompound(0).getString("id");
						String string4 = (String)field_19363.get(string3);
						if (string4 != null) {
							nbtCompound2.putString("id", string4);
						}
					}

					String string5 = nbtCompound2.getString("id");
					((Long2ObjectMap)this.field_19365.computeIfAbsent(string5, stringx -> new Long2ObjectOpenHashMap())).put(l, nbtCompound2);
				}

				String string6 = string + "_index";
				class_3990 lv = arg.method_17977(DimensionType.OVERWORLD, class_3990::new, string6);
				if (lv != null && !lv.method_17642().isEmpty()) {
					this.field_19366.put(string, lv);
				} else {
					class_3990 lv2 = new class_3990(string6);
					this.field_19366.put(string, lv2);

					for (String string7 : nbtCompound.getKeys()) {
						NbtCompound nbtCompound3 = nbtCompound.getCompound(string7);
						lv2.method_17643(ChunkPos.getIdFromCoords(nbtCompound3.getInt("ChunkX"), nbtCompound3.getInt("ChunkZ")));
					}

					arg.method_17976(DimensionType.OVERWORLD, string6, lv2);
					lv2.markDirty();
				}
			}
		}
	}

	public static class_3979 method_17612(DimensionType dimensionType, @Nullable class_4070 arg) {
		if (dimensionType == DimensionType.OVERWORLD) {
			return new class_3979.class_3981(arg);
		} else if (dimensionType == DimensionType.THE_NETHER) {
			return new class_3979.class_3980(arg);
		} else if (dimensionType == DimensionType.THE_END) {
			return new class_3979.class_3982(arg);
		} else {
			throw new RuntimeException(String.format("Unknown dimension type : %s", dimensionType));
		}
	}

	public static class class_3980 extends class_3979 {
		private static final String[] field_19367 = new String[]{"Fortress"};

		public class_3980(@Nullable class_4070 arg) {
			super(arg);
		}

		@Override
		protected String[] method_17608() {
			return field_19367;
		}

		@Override
		protected String[] method_17618() {
			return field_19367;
		}
	}

	public static class class_3981 extends class_3979 {
		private static final String[] field_19368 = new String[]{"Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"};
		private static final String[] field_19369 = new String[]{
			"Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"
		};

		public class_3981(@Nullable class_4070 arg) {
			super(arg);
		}

		@Override
		protected String[] method_17608() {
			return field_19368;
		}

		@Override
		protected String[] method_17618() {
			return field_19369;
		}
	}

	public static class class_3982 extends class_3979 {
		private static final String[] field_19370 = new String[]{"EndCity"};

		public class_3982(@Nullable class_4070 arg) {
			super(arg);
		}

		@Override
		protected String[] method_17608() {
			return field_19370;
		}

		@Override
		protected String[] method_17618() {
			return field_19370;
		}
	}
}
