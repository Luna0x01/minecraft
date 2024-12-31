package net.minecraft.client.network;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ServerInfo {
	public String name;
	public String address;
	public String playerCountLabel;
	public String label;
	public long ping;
	public int protocolVersion = 47;
	public String version = "1.8.9";
	public boolean online;
	public String playerListSummary;
	private ServerInfo.ResourcePackState resourcePackState = ServerInfo.ResourcePackState.PROMPT;
	private String icon;
	private boolean local;

	public ServerInfo(String string, String string2, boolean bl) {
		this.name = string;
		this.address = string2;
		this.local = bl;
	}

	public NbtCompound serialize() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("name", this.name);
		nbtCompound.putString("ip", this.address);
		if (this.icon != null) {
			nbtCompound.putString("icon", this.icon);
		}

		if (this.resourcePackState == ServerInfo.ResourcePackState.ENABLED) {
			nbtCompound.putBoolean("acceptTextures", true);
		} else if (this.resourcePackState == ServerInfo.ResourcePackState.DISABLED) {
			nbtCompound.putBoolean("acceptTextures", false);
		}

		return nbtCompound;
	}

	public ServerInfo.ResourcePackState getResourcePack() {
		return this.resourcePackState;
	}

	public void setResourcePackState(ServerInfo.ResourcePackState state) {
		this.resourcePackState = state;
	}

	public static ServerInfo deserialize(NbtCompound nbt) {
		ServerInfo serverInfo = new ServerInfo(nbt.getString("name"), nbt.getString("ip"), false);
		if (nbt.contains("icon", 8)) {
			serverInfo.setIcon(nbt.getString("icon"));
		}

		if (nbt.contains("acceptTextures", 1)) {
			if (nbt.getBoolean("acceptTextures")) {
				serverInfo.setResourcePackState(ServerInfo.ResourcePackState.ENABLED);
			} else {
				serverInfo.setResourcePackState(ServerInfo.ResourcePackState.DISABLED);
			}
		} else {
			serverInfo.setResourcePackState(ServerInfo.ResourcePackState.PROMPT);
		}

		return serverInfo;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isLocal() {
		return this.local;
	}

	public void copyFrom(ServerInfo info) {
		this.address = info.address;
		this.name = info.name;
		this.setResourcePackState(info.getResourcePack());
		this.icon = info.icon;
		this.local = info.local;
	}

	public static enum ResourcePackState {
		ENABLED("enabled"),
		DISABLED("disabled"),
		PROMPT("prompt");

		private final Text name;

		private ResourcePackState(String string2) {
			this.name = new TranslatableText("addServer.resourcePack." + string2);
		}

		public Text getName() {
			return this.name;
		}
	}
}
