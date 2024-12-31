package net.minecraft.client.util;

import net.minecraft.client.twitch.StreamMetadata;
import net.minecraft.client.twitch.TwitchStream;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.chat.ChatUserInfo;

public interface TwitchStreamProvider {
	void stop();

	void update();

	void submit();

	boolean isLoginSuccessful();

	boolean isReady();

	boolean isLive();

	void method_7248(StreamMetadata streamData, long l);

	void method_10470(StreamMetadata streamData, long l, long m);

	boolean isPaused();

	void requestCommercial();

	void pauseBroadcast();

	void resumeBroadcast();

	void setStreamVolume();

	void initializeStreamProperties();

	void stopStream();

	IngestServer[] getIngestServers();

	void setStreamListener();

	TwitchStream getTwitchStream();

	boolean isTesting();

	int getViewerCount();

	boolean isChannelNameSet();

	String getCurrentChannelName();

	ChatUserInfo getUserInfo(String username);

	void sendChatMessage(String chatMessage);

	boolean isRunning();

	ErrorCode getErrorCode();

	boolean isLoggedIn();

	void toggleMic(boolean micEnabled);

	boolean isMuted();

	TwitchStreamProvider.Reason getReason();

	public static enum Reason {
		ERROR,
		INVALID_TOKEN;
	}
}
