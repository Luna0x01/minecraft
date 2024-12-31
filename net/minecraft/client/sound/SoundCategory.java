package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import java.util.Map;

public enum SoundCategory {
	MASTER("master", 0),
	MUSIC("music", 1),
	RECORDS("record", 2),
	WEATHER("weather", 3),
	BLOCKS("block", 4),
	MOBS("hostile", 5),
	ANIMALS("neutral", 6),
	PLAYERS("player", 7),
	AMBIENT("ambient", 8);

	private static final Map<String, SoundCategory> NAME_MAP = Maps.newHashMap();
	private static final Map<Integer, SoundCategory> CATEGORY_MAP = Maps.newHashMap();
	private final String name;
	private final int id;

	private SoundCategory(String string2, int j) {
		this.name = string2;
		this.id = j;
	}

	public String getName() {
		return this.name;
	}

	public int getId() {
		return this.id;
	}

	public static SoundCategory byName(String name) {
		return (SoundCategory)NAME_MAP.get(name);
	}

	static {
		for (SoundCategory soundCategory : values()) {
			if (NAME_MAP.containsKey(soundCategory.getName()) || CATEGORY_MAP.containsKey(soundCategory.getId())) {
				throw new Error("Clash in Sound Category ID & Name pools! Cannot insert " + soundCategory);
			}

			NAME_MAP.put(soundCategory.getName(), soundCategory);
			CATEGORY_MAP.put(soundCategory.getId(), soundCategory);
		}
	}
}
