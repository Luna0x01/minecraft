package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.client.resource.DirectResourceIndex;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.util.Session;

public class RunArgs {
	public final RunArgs.Network network;
	public final WindowSettings windowSettings;
	public final RunArgs.Directories directories;
	public final RunArgs.Game game;
	public final RunArgs.AutoConnect autoConnect;

	public RunArgs(RunArgs.Network network, WindowSettings windowSettings, RunArgs.Directories dirs, RunArgs.Game game, RunArgs.AutoConnect autoConnect) {
		this.network = network;
		this.windowSettings = windowSettings;
		this.directories = dirs;
		this.game = game;
		this.autoConnect = autoConnect;
	}

	public static class AutoConnect {
		@Nullable
		public final String serverAddress;
		public final int serverPort;

		public AutoConnect(@Nullable String serverAddress, int serverPort) {
			this.serverAddress = serverAddress;
			this.serverPort = serverPort;
		}
	}

	public static class Directories {
		public final File runDir;
		public final File resourcePackDir;
		public final File assetDir;
		@Nullable
		public final String assetIndex;

		public Directories(File runDir, File resPackDir, File assetDir, @Nullable String assetIndex) {
			this.runDir = runDir;
			this.resourcePackDir = resPackDir;
			this.assetDir = assetDir;
			this.assetIndex = assetIndex;
		}

		public ResourceIndex getResourceIndex() {
			return (ResourceIndex)(this.assetIndex == null ? new DirectResourceIndex(this.assetDir) : new ResourceIndex(this.assetDir, this.assetIndex));
		}
	}

	public static class Game {
		public final boolean demo;
		public final String version;
		public final String versionType;
		public final boolean multiplayerDisabled;
		public final boolean onlineChatDisabled;

		public Game(boolean demo, String version, String versionType, boolean multiplayerDisabled, boolean onlineChatDisabled) {
			this.demo = demo;
			this.version = version;
			this.versionType = versionType;
			this.multiplayerDisabled = multiplayerDisabled;
			this.onlineChatDisabled = onlineChatDisabled;
		}
	}

	public static class Network {
		public final Session session;
		public final PropertyMap userProperties;
		public final PropertyMap profileProperties;
		public final Proxy netProxy;

		public Network(Session session, PropertyMap userProperties, PropertyMap profileProperties, Proxy proxy) {
			this.session = session;
			this.userProperties = userProperties;
			this.profileProperties = profileProperties;
			this.netProxy = proxy;
		}
	}
}
