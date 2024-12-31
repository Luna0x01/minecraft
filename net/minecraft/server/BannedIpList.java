package net.minecraft.server;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class BannedIpList extends ServerConfigList<String, BannedIpEntry> {
	public BannedIpList(File file) {
		super(file);
	}

	@Override
	protected ServerConfigEntry<String> fromJson(JsonObject jsonObject) {
		return new BannedIpEntry(jsonObject);
	}

	public boolean isBanned(SocketAddress ip) {
		String string = this.stringifyAddress(ip);
		return this.contains(string);
	}

	public boolean method_21380(String string) {
		return this.contains(string);
	}

	public BannedIpEntry get(SocketAddress address) {
		String string = this.stringifyAddress(address);
		return this.get(string);
	}

	private String stringifyAddress(SocketAddress address) {
		String string = address.toString();
		if (string.contains("/")) {
			string = string.substring(string.indexOf(47) + 1);
		}

		if (string.contains(":")) {
			string = string.substring(0, string.indexOf(58));
		}

		return string;
	}
}
