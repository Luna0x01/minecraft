package net.minecraft.client.util;

import net.minecraft.client.twitch.StreamMetadata;
import net.minecraft.client.twitch.TwitchStream;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.chat.ChatUserInfo;

public class NullTwitchStream implements TwitchStreamProvider {
	private final Throwable throwable;

	public NullTwitchStream(Throwable throwable) {
		this.throwable = throwable;
	}

	@Override
	public void stop() {
	}

	@Override
	public void update() {
	}

	@Override
	public void submit() {
	}

	@Override
	public boolean isLoginSuccessful() {
		return false;
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public boolean isLive() {
		return false;
	}

	@Override
	public void method_7248(StreamMetadata streamData, long l) {
	}

	@Override
	public void method_10470(StreamMetadata streamData, long l, long m) {
	}

	@Override
	public boolean isPaused() {
		return false;
	}

	@Override
	public void requestCommercial() {
	}

	@Override
	public void pauseBroadcast() {
	}

	@Override
	public void resumeBroadcast() {
	}

	@Override
	public void setStreamVolume() {
	}

	@Override
	public void initializeStreamProperties() {
	}

	@Override
	public void stopStream() {
	}

	@Override
	public IngestServer[] getIngestServers() {
		return new IngestServer[0];
	}

	@Override
	public void setStreamListener() {
	}

	@Override
	public TwitchStream getTwitchStream() {
		return null;
	}

	@Override
	public boolean isTesting() {
		return false;
	}

	@Override
	public int getViewerCount() {
		return 0;
	}

	@Override
	public boolean isChannelNameSet() {
		return false;
	}

	@Override
	public String getCurrentChannelName() {
		return null;
	}

	@Override
	public ChatUserInfo getUserInfo(String username) {
		return null;
	}

	@Override
	public void sendChatMessage(String chatMessage) {
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public ErrorCode getErrorCode() {
		return null;
	}

	@Override
	public boolean isLoggedIn() {
		return false;
	}

	@Override
	public void toggleMic(boolean micEnabled) {
	}

	@Override
	public boolean isMuted() {
		return false;
	}

	@Override
	public TwitchStreamProvider.Reason getReason() {
		return TwitchStreamProvider.Reason.ERROR;
	}

	public Throwable getThrowable() {
		return this.throwable;
	}
}
