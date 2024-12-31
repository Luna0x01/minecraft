package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;

public enum SoundCategory {
	MASTER("master"),
	MUSIC("music"),
	RECORDS("record"),
	WEATHER("weather"),
	BLOCKS("block"),
	HOSTILE("hostile"),
	NEUTRAL("neutral"),
	PLAYERS("player"),
	AMBIENT("ambient"),
	VOICE("voice");

	private static final Map<String, SoundCategory> NAME_MAP = Maps.newHashMap();
	private final String name;

	private SoundCategory(String string2) {
		this.name = string2;
	}

	public String getName() {
		return this.name;
	}

	public static SoundCategory byName(String name) {
		return (SoundCategory)NAME_MAP.get(name);
	}

	public static Set<String> method_12844() {
		return NAME_MAP.keySet();
	}

	static {
		for (SoundCategory soundCategory : values()) {
			if (NAME_MAP.containsKey(soundCategory.getName())) {
				throw new Error("Clash in Sound Category name pools! Cannot insert " + soundCategory);
			}

			NAME_MAP.put(soundCategory.getName(), soundCategory);
		}
	}
}
