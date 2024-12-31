package net.minecraft.client.network;

import net.minecraft.util.Util;

public class LanServerInfo {
	private final String motd;
	private final String addressPort;
	private long lastTimeMillis;

	public LanServerInfo(String string, String string2) {
		this.motd = string;
		this.addressPort = string2;
		this.lastTimeMillis = Util.getMeasuringTimeMs();
	}

	public String getMotd() {
		return this.motd;
	}

	public String getAddressPort() {
		return this.addressPort;
	}

	public void updateLastTime() {
		this.lastTimeMillis = Util.getMeasuringTimeMs();
	}
}
