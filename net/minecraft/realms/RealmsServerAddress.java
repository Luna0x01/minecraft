package net.minecraft.realms;

import net.minecraft.network.ServerAddress;

public class RealmsServerAddress {
	private final String host;
	private final int port;

	protected RealmsServerAddress(String string, int i) {
		this.host = string;
		this.port = i;
	}

	public String getHost() {
		return this.host;
	}

	public int getPort() {
		return this.port;
	}

	public static RealmsServerAddress parseString(String string) {
		ServerAddress serverAddress = ServerAddress.parse(string);
		return new RealmsServerAddress(serverAddress.getAddress(), serverAddress.getPort());
	}
}
