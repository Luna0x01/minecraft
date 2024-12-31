package net.minecraft.client.network;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ServerInfo {
	public String name;
	public String address;
	public Text playerCountLabel;
	public Text label;
	public long ping;
	public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
	public Text version = new LiteralText(SharedConstants.getGameVersion().getName());
	public boolean online;
	public List<Text> playerListSummary = Collections.emptyList();
	private ServerInfo.ResourcePackPolicy resourcePackPolicy = ServerInfo.ResourcePackPolicy.PROMPT;
	@Nullable
	private String icon;
	private boolean local;

	public ServerInfo(String name, String address, boolean local) {
		this.name = name;
		this.address = address;
		this.local = local;
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("name", this.name);
		nbtCompound.putString("ip", this.address);
		if (this.icon != null) {
			nbtCompound.putString("icon", this.icon);
		}

		if (this.resourcePackPolicy == ServerInfo.ResourcePackPolicy.ENABLED) {
			nbtCompound.putBoolean("acceptTextures", true);
		} else if (this.resourcePackPolicy == ServerInfo.ResourcePackPolicy.DISABLED) {
			nbtCompound.putBoolean("acceptTextures", false);
		}

		return nbtCompound;
	}

	public ServerInfo.ResourcePackPolicy getResourcePackPolicy() {
		return this.resourcePackPolicy;
	}

	public void setResourcePackPolicy(ServerInfo.ResourcePackPolicy policy) {
		this.resourcePackPolicy = policy;
	}

	public static ServerInfo fromNbt(NbtCompound root) {
		ServerInfo serverInfo = new ServerInfo(root.getString("name"), root.getString("ip"), false);
		if (root.contains("icon", 8)) {
			serverInfo.setIcon(root.getString("icon"));
		}

		if (root.contains("acceptTextures", 1)) {
			if (root.getBoolean("acceptTextures")) {
				serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.ENABLED);
			} else {
				serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.DISABLED);
			}
		} else {
			serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.PROMPT);
		}

		return serverInfo;
	}

	@Nullable
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(@Nullable String icon) {
		this.icon = icon;
	}

	public boolean isLocal() {
		return this.local;
	}

	public void copyFrom(ServerInfo serverInfo) {
		this.address = serverInfo.address;
		this.name = serverInfo.name;
		this.setResourcePackPolicy(serverInfo.getResourcePackPolicy());
		this.icon = serverInfo.icon;
		this.local = serverInfo.local;
	}

	public static enum ResourcePackPolicy {
		ENABLED("enabled"),
		DISABLED("disabled"),
		PROMPT("prompt");

		private final Text name;

		private ResourcePackPolicy(String name) {
			this.name = new TranslatableText("addServer.resourcePack." + name);
		}

		public Text getName() {
			return this.name;
		}
	}
}
