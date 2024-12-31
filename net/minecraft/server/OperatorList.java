package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class OperatorList extends ServerConfigList<GameProfile, OperatorEntry> {
	public OperatorList(File file) {
		super(file);
	}

	@Override
	protected ServerConfigEntry<GameProfile> fromJson(JsonObject jsonObject) {
		return new OperatorEntry(jsonObject);
	}

	@Override
	public String[] getNames() {
		String[] strings = new String[this.values().size()];
		int i = 0;

		for (OperatorEntry operatorEntry : this.values().values()) {
			strings[i++] = operatorEntry.getKey().getName();
		}

		return strings;
	}

	public int method_12832(GameProfile gameProfile) {
		OperatorEntry operatorEntry = this.get(gameProfile);
		return operatorEntry != null ? operatorEntry.getPermissionLevel() : 0;
	}

	public boolean isOp(GameProfile profile) {
		OperatorEntry operatorEntry = this.get(profile);
		return operatorEntry != null ? operatorEntry.canBypassPlayerLimit() : false;
	}

	protected String toString(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}

	public GameProfile getOperatorPlayer(String playerName) {
		for (OperatorEntry operatorEntry : this.values().values()) {
			if (playerName.equalsIgnoreCase(operatorEntry.getKey().getName())) {
				return operatorEntry.getKey();
			}
		}

		return null;
	}
}
