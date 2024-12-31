package net.minecraft.server;

import com.google.gson.JsonObject;
import java.util.Date;

public class BannedIpEntry extends BanEntry<String> {
	public BannedIpEntry(String string) {
		this(string, null, null, null, null);
	}

	public BannedIpEntry(String string, Date date, String string2, Date date2, String string3) {
		super(string, date, string2, date2, string3);
	}

	public BannedIpEntry(JsonObject jsonObject) {
		super(getIpFromJson(jsonObject), jsonObject);
	}

	private static String getIpFromJson(JsonObject json) {
		return json.has("ip") ? json.get("ip").getAsString() : null;
	}

	@Override
	protected void serialize(JsonObject jsonObject) {
		if (this.getKey() != null) {
			jsonObject.addProperty("ip", this.getKey());
			super.serialize(jsonObject);
		}
	}
}
