package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class Whitelist extends ServerConfigList<GameProfile, WhitelistEntry> {
	public Whitelist(File file) {
		super(file);
	}

	@Override
	protected ServerConfigEntry<GameProfile> fromJson(JsonObject jsonObject) {
		return new WhitelistEntry(jsonObject);
	}

	@Override
	public String[] getNames() {
		String[] strings = new String[this.values().size()];
		int i = 0;

		for (WhitelistEntry whitelistEntry : this.values().values()) {
			strings[i++] = whitelistEntry.getKey().getName();
		}

		return strings;
	}

	protected String toString(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}

	public GameProfile getProfile(String value) {
		for (WhitelistEntry whitelistEntry : this.values().values()) {
			if (value.equalsIgnoreCase(whitelistEntry.getKey().getName())) {
				return whitelistEntry.getKey();
			}
		}

		return null;
	}
}
