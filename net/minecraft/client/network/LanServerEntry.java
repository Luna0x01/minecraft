package net.minecraft.client.network;

import net.minecraft.util.SystemUtil;

public class LanServerEntry {
	private final String motd;
	private final String addressPort;
	private long lastTimeMillis;

	public LanServerEntry(String string, String string2) {
		this.motd = string;
		this.addressPort = string2;
		this.lastTimeMillis = SystemUtil.getMeasuringTimeMs();
	}

	public String getMotd() {
		return this.motd;
	}

	public String getAddressPort() {
		return this.addressPort;
	}

	public void updateLastTime() {
		this.lastTimeMillis = SystemUtil.getMeasuringTimeMs();
	}
}
