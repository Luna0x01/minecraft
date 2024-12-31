package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.client.resource.AssetsIndex;
import net.minecraft.client.util.Session;

public class RunArgs {
	public final RunArgs.Args args;
	public final RunArgs.WindowInformation windowInformation;
	public final RunArgs.Directories directories;
	public final RunArgs.Game game;
	public final RunArgs.AutoConnect autoConnect;

	public RunArgs(
		RunArgs.Args args, RunArgs.WindowInformation windowInformation, RunArgs.Directories directories, RunArgs.Game game, RunArgs.AutoConnect autoConnect
	) {
		this.args = args;
		this.windowInformation = windowInformation;
		this.directories = directories;
		this.game = game;
		this.autoConnect = autoConnect;
	}

	public static class Args {
		public final Session session;
		public final PropertyMap userProperties;
		public final PropertyMap profileProperties;
		public final Proxy netProxy;

		public Args(Session session, PropertyMap propertyMap, PropertyMap propertyMap2, Proxy proxy) {
			this.session = session;
			this.userProperties = propertyMap;
			this.profileProperties = propertyMap2;
			this.netProxy = proxy;
		}
	}

	public static class AutoConnect {
		public final String serverIp;
		public final int serverPort;

		public AutoConnect(String string, int i) {
			this.serverIp = string;
			this.serverPort = i;
		}
	}

	public static class Directories {
		public final File runDir;
		public final File resourcePackDir;
		public final File assetDir;
		public final String assetIndex;

		public Directories(File file, File file2, File file3, @Nullable String string) {
			this.runDir = file;
			this.resourcePackDir = file2;
			this.assetDir = file3;
			this.assetIndex = string;
		}

		public AssetsIndex getAssetsIndex() {
			return (AssetsIndex)(this.assetIndex == null ? new class_2902(this.assetDir) : new AssetsIndex(this.assetDir, this.assetIndex));
		}
	}

	public static class Game {
		public final boolean demo;
		public final String version;
		public final String versionType;

		public Game(boolean bl, String string, String string2) {
			this.demo = bl;
			this.version = string;
			this.versionType = string2;
		}
	}

	public static class WindowInformation {
		public final int width;
		public final int height;
		public final boolean fullscreen;
		public final boolean checkGlErrors;

		public WindowInformation(int i, int j, boolean bl, boolean bl2) {
			this.width = i;
			this.height = j;
			this.fullscreen = bl;
			this.checkGlErrors = bl2;
		}
	}
}
