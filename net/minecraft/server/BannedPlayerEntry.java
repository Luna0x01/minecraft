package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;

public class BannedPlayerEntry extends BanEntry<GameProfile> {
	public BannedPlayerEntry(GameProfile gameProfile) {
		this(gameProfile, null, null, null, null);
	}

	public BannedPlayerEntry(GameProfile gameProfile, Date date, String string, Date date2, String string2) {
		super(gameProfile, date2, string, date2, string2);
	}

	public BannedPlayerEntry(JsonObject jsonObject) {
		super(getProfileFromJson(jsonObject), jsonObject);
	}

	@Override
	protected void serialize(JsonObject jsonObject) {
		if (this.getKey() != null) {
			jsonObject.addProperty("uuid", this.getKey().getId() == null ? "" : this.getKey().getId().toString());
			jsonObject.addProperty("name", this.getKey().getName());
			super.serialize(jsonObject);
		}
	}

	private static GameProfile getProfileFromJson(JsonObject json) {
		if (json.has("uuid") && json.has("name")) {
			String string = json.get("uuid").getAsString();

			UUID uUID;
			try {
				uUID = UUID.fromString(string);
			} catch (Throwable var4) {
				return null;
			}

			return new GameProfile(uUID, json.get("name").getAsString());
		} else {
			return null;
		}
	}
}
