package net.minecraft.realms;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Proxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Session;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.LevelInfo;

public class Realms {
	public static boolean isTouchScreen() {
		return MinecraftClient.getInstance().options.touchscreen;
	}

	public static Proxy getProxy() {
		return MinecraftClient.getInstance().getNetworkProxy();
	}

	public static String sessionId() {
		Session session = MinecraftClient.getInstance().getSession();
		return session == null ? null : session.getSessionId();
	}

	public static String userName() {
		Session session = MinecraftClient.getInstance().getSession();
		return session == null ? null : session.getUsername();
	}

	public static long currentTimeMillis() {
		return MinecraftClient.getTime();
	}

	public static String getSessionId() {
		return MinecraftClient.getInstance().getSession().getSessionId();
	}

	public static String getUUID() {
		return MinecraftClient.getInstance().getSession().getUuid();
	}

	public static String getName() {
		return MinecraftClient.getInstance().getSession().getUsername();
	}

	public static String uuidToName(String uuid) {
		return MinecraftClient.getInstance().getSessionService().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(uuid), null), false).getName();
	}

	public static void setScreen(RealmsScreen realmsScreen) {
		MinecraftClient.getInstance().setScreen(realmsScreen.getProxy());
	}

	public static String getGameDirectoryPath() {
		return MinecraftClient.getInstance().runDirectory.getAbsolutePath();
	}

	public static int survivalId() {
		return LevelInfo.GameMode.SURVIVAL.getId();
	}

	public static int creativeId() {
		return LevelInfo.GameMode.CREATIVE.getId();
	}

	public static int adventureId() {
		return LevelInfo.GameMode.ADVENTURE.getId();
	}

	public static int spectatorId() {
		return LevelInfo.GameMode.SPECTATOR.getId();
	}

	public static void setConnectedToRealms(boolean connectedToRealms) {
		MinecraftClient.getInstance().setConnectedToRealms(connectedToRealms);
	}

	public static ListenableFuture<Object> downloadResourcePack(String string, String string2) {
		return MinecraftClient.getInstance().getResourcePackLoader().downloadResourcePack(string, string2);
	}

	public static void clearResourcePack() {
		MinecraftClient.getInstance().getResourcePackLoader().clear();
	}

	public static boolean getRealmsNotificationsEnabled() {
		return MinecraftClient.getInstance().options.getIntVideoOptions(GameOptions.Option.REALMS_NOTIFICATIONS);
	}

	public static boolean inTitleScreen() {
		return MinecraftClient.getInstance().currentScreen != null && MinecraftClient.getInstance().currentScreen instanceof TitleScreen;
	}

	public static void deletePlayerTag(File file) {
		if (file.exists()) {
			try {
				NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(file));
				NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
				nbtCompound2.remove("Player");
				NbtIo.writeCompressed(nbtCompound, new FileOutputStream(file));
			} catch (Exception var3) {
				var3.printStackTrace();
			}
		}
	}
}
