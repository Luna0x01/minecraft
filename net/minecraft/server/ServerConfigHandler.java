package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ChatUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConfigHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final File BANNED_IPS_FILE = new File("banned-ips.txt");
	public static final File BANNED_PLAYERS_FILE = new File("banned-players.txt");
	public static final File OPERATORS_FILE = new File("ops.txt");
	public static final File WHITE_LIST_FILE = new File("white-list.txt");

	private static void lookupProfile(MinecraftServer server, Collection<String> bannedPlayers, ProfileLookupCallback callback) {
		String[] strings = (String[])Iterators.toArray(Iterators.filter(bannedPlayers.iterator(), new Predicate<String>() {
			public boolean apply(@Nullable String string) {
				return !ChatUtil.isEmpty(string);
			}
		}), String.class);
		if (server.isOnlineMode()) {
			server.getGameProfileRepo().findProfilesByNames(strings, Agent.MINECRAFT, callback);
		} else {
			for (String string : strings) {
				UUID uUID = PlayerEntity.getUuidFromProfile(new GameProfile(null, string));
				GameProfile gameProfile = new GameProfile(uUID, string);
				callback.onProfileLookupSucceeded(gameProfile);
			}
		}
	}

	public static String method_8204(MinecraftServer minecraftServer, String string) {
		if (!ChatUtil.isEmpty(string) && string.length() <= 16) {
			GameProfile gameProfile = minecraftServer.getUserCache().findByName(string);
			if (gameProfile != null && gameProfile.getId() != null) {
				return gameProfile.getId().toString();
			} else if (!minecraftServer.isSinglePlayer() && minecraftServer.isOnlineMode()) {
				final List<GameProfile> list = Lists.newArrayList();
				ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
					public void onProfileLookupSucceeded(GameProfile profile) {
						minecraftServer.getUserCache().add(profile);
						list.add(profile);
					}

					public void onProfileLookupFailed(GameProfile profile, Exception exception) {
						ServerConfigHandler.LOGGER.warn("Could not lookup user whitelist entry for {}", profile.getName(), exception);
					}
				};
				lookupProfile(minecraftServer, Lists.newArrayList(new String[]{string}), profileLookupCallback);
				return !list.isEmpty() && ((GameProfile)list.get(0)).getId() != null ? ((GameProfile)list.get(0)).getId().toString() : "";
			} else {
				return PlayerEntity.getUuidFromProfile(new GameProfile(null, string)).toString();
			}
		} else {
			return string;
		}
	}
}
