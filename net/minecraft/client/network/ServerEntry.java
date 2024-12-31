package net.minecraft.client.network;

import net.minecraft.client.MinecraftClient;

public class ServerEntry {
	private final String name;
	private final String address;
	private long time;

	public ServerEntry(String string, String string2) {
		this.name = string;
		this.address = string2;
		this.time = MinecraftClient.getTime();
	}

	public String getName() {
		return this.name;
	}

	public String getAddress() {
		return this.address;
	}

	public void updateTime() {
		this.time = MinecraftClient.getTime();
	}
}
