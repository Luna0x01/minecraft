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
		String[] strings = new String[this.method_21390().size()];
		int i = 0;

		for (ServerConfigEntry<GameProfile> serverConfigEntry : this.method_21390()) {
			strings[i++] = serverConfigEntry.getKey().getName();
		}

		return strings;
	}

	public boolean isOp(GameProfile profile) {
		OperatorEntry operatorEntry = this.get(profile);
		return operatorEntry != null ? operatorEntry.canBypassPlayerLimit() : false;
	}

	protected String toString(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}
}
