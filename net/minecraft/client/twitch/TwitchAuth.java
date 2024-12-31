package net.minecraft.client.twitch;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.StreamUtilitiesScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.client.util.TwitchStreamProvider;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL11;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfo;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.VideoParams;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.ChatUserMode;
import tv.twitch.chat.ChatUserSubscription;

public class TwitchAuth implements StreamController.Listener, Twitch.Listener, TwitchStream.Listener, TwitchStreamProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Marker streamMarker = MarkerManager.getMarker("STREAM");
	private final StreamController streamController;
	private final Twitch twitch;
	private String currentChannelName;
	private final MinecraftClient client;
	private final Text twitchText = new LiteralText("Twitch");
	private final Map<String, ChatUserInfo> twitchChat = Maps.newHashMap();
	private Framebuffer framebuffer;
	private boolean streamSendMetadata;
	private int targetFps = 30;
	private long field_8351 = 0L;
	private boolean streamFinished = false;
	private boolean loggedIn;
	private boolean paused;
	private boolean micEnabled;
	private TwitchStreamProvider.Reason reason = TwitchStreamProvider.Reason.ERROR;
	private static boolean initialized;

	public TwitchAuth(MinecraftClient minecraftClient, Property property) {
		this.client = minecraftClient;
		this.streamController = new StreamController();
		this.twitch = new Twitch();
		this.streamController.setListener(this);
		this.twitch.setListener(this);
		this.streamController.setClientId("nmt37qblda36pvonovdkbopzfzw3wlq");
		this.twitch.setClientId("nmt37qblda36pvonovdkbopzfzw3wlq");
		this.twitchText.getStyle().setFormatting(Formatting.DARK_PURPLE);
		if (property != null && !Strings.isNullOrEmpty(property.getValue()) && GLX.advanced) {
			Thread thread = new Thread("Twitch authenticator") {
				public void run() {
					try {
						URL uRL = new URL("https://api.twitch.tv/kraken?oauth_token=" + URLEncoder.encode(property.getValue(), "UTF-8"));
						String string = NetworkUtils.get(uRL);
						JsonObject jsonObject = JsonHelper.asObject(new JsonParser().parse(string), "Response");
						JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "token");
						if (JsonHelper.getBoolean(jsonObject2, "valid")) {
							String string2 = JsonHelper.getString(jsonObject2, "user_name");
							TwitchAuth.LOGGER.debug(TwitchAuth.streamMarker, "Authenticated with twitch; username is {}", new Object[]{string2});
							AuthToken authToken = new AuthToken();
							authToken.data = property.getValue();
							TwitchAuth.this.streamController.setAuthentication(string2, authToken);
							TwitchAuth.this.twitch.setUsername(string2);
							TwitchAuth.this.twitch.setAuthToken(authToken);
							Runtime.getRuntime().addShutdownHook(new Thread("Twitch shutdown hook") {
								public void run() {
									TwitchAuth.this.stop();
								}
							});
							TwitchAuth.this.streamController.start();
							TwitchAuth.this.twitch.method_10444();
						} else {
							TwitchAuth.this.reason = TwitchStreamProvider.Reason.INVALID_TOKEN;
							TwitchAuth.LOGGER.error(TwitchAuth.streamMarker, "Given twitch access token is invalid");
						}
					} catch (IOException var7) {
						TwitchAuth.this.reason = TwitchStreamProvider.Reason.ERROR;
						TwitchAuth.LOGGER.error(TwitchAuth.streamMarker, "Could not authenticate with twitch", var7);
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}

	@Override
	public void stop() {
		LOGGER.debug(streamMarker, "Shutdown streaming");
		this.streamController.method_10435();
		this.twitch.stop();
	}

	@Override
	public void update() {
		int i = this.client.options.streamChatEnabled;
		boolean bl = this.currentChannelName != null && this.twitch.isChatListenerConnected(this.currentChannelName);
		boolean bl2 = this.twitch.getStreamState() == Twitch.StreamState.INITIALIZED
			&& (this.currentChannelName == null || this.twitch.getChatNetworkState(this.currentChannelName) == Twitch.NetworkState.DISCONNECTED);
		if (i == 2) {
			if (bl) {
				LOGGER.debug(streamMarker, "Disconnecting from twitch chat per user options");
				this.twitch.method_10443(this.currentChannelName);
			}
		} else if (i == 1) {
			if (bl2 && this.streamController.isLoginSuccessful()) {
				LOGGER.debug(streamMarker, "Connecting to twitch chat per user options");
				this.method_7269();
			}
		} else if (i == 0) {
			if (bl && !this.isLive()) {
				LOGGER.debug(streamMarker, "Disconnecting from twitch chat as user is no longer streaming");
				this.twitch.method_10443(this.currentChannelName);
			} else if (bl2 && this.isLive()) {
				LOGGER.debug(streamMarker, "Connecting to twitch chat as user is streaming");
				this.method_7269();
			}
		}

		this.streamController.method_7152();
		this.twitch.flushChatEvents();
	}

	protected void method_7269() {
		Twitch.StreamState streamState = this.twitch.getStreamState();
		String string = this.streamController.getChannelInfo().name;
		this.currentChannelName = string;
		if (streamState != Twitch.StreamState.INITIALIZED) {
			LOGGER.warn("Invalid twitch chat state {}", new Object[]{streamState});
		} else if (this.twitch.getChatNetworkState(this.currentChannelName) == Twitch.NetworkState.DISCONNECTED) {
			this.twitch.method_7205(string);
		} else {
			LOGGER.warn("Invalid twitch chat state {}", new Object[]{streamState});
		}
	}

	@Override
	public void submit() {
		if (this.streamController.isLive() && !this.streamController.isPaused()) {
			long l = System.nanoTime();
			long m = (long)(1000000000 / this.targetFps);
			long n = l - this.field_8351;
			boolean bl = n >= m;
			if (bl) {
				FrameBuffer frameBuffer = this.streamController.method_7157();
				Framebuffer framebuffer = this.client.getFramebuffer();
				this.framebuffer.bind(true);
				GlStateManager.matrixMode(5889);
				GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();
				GlStateManager.ortho(0.0, (double)this.framebuffer.viewportWidth, (double)this.framebuffer.viewportHeight, 0.0, 1000.0, 3000.0);
				GlStateManager.matrixMode(5888);
				GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();
				GlStateManager.translate(0.0F, 0.0F, -2000.0F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.viewport(0, 0, this.framebuffer.viewportWidth, this.framebuffer.viewportHeight);
				GlStateManager.enableTexture();
				GlStateManager.disableAlphaTest();
				GlStateManager.disableBlend();
				float f = (float)this.framebuffer.viewportWidth;
				float g = (float)this.framebuffer.viewportHeight;
				float h = (float)framebuffer.viewportWidth / (float)framebuffer.textureWidth;
				float i = (float)framebuffer.viewportHeight / (float)framebuffer.textureHeight;
				framebuffer.beginRead();
				GL11.glTexParameterf(3553, 10241, 9729.0F);
				GL11.glTexParameterf(3553, 10240, 9729.0F);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
				bufferBuilder.vertex(0.0, (double)g, 0.0).texture(0.0, (double)i).next();
				bufferBuilder.vertex((double)f, (double)g, 0.0).texture((double)h, (double)i).next();
				bufferBuilder.vertex((double)f, 0.0, 0.0).texture((double)h, 0.0).next();
				bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0, 0.0).next();
				tessellator.draw();
				framebuffer.endRead();
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5889);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
				this.streamController.method_7168(frameBuffer);
				this.framebuffer.unbind();
				this.streamController.method_7173(frameBuffer);
				this.field_8351 = l;
			}
		}
	}

	@Override
	public boolean isLoginSuccessful() {
		return this.streamController.isLoginSuccessful();
	}

	@Override
	public boolean isReady() {
		return this.streamController.isReady();
	}

	@Override
	public boolean isLive() {
		return this.streamController.isLive();
	}

	@Override
	public void method_7248(StreamMetadata streamData, long l) {
		if (this.isLive() && this.streamSendMetadata) {
			long m = this.streamController.getStreamTime();
			if (!this.streamController.method_7164(streamData.getName(), m + l, streamData.getDescription(), streamData.toJson())) {
				LOGGER.warn(streamMarker, "Couldn't send stream metadata action at {}: {}", new Object[]{m + l, streamData});
			} else {
				LOGGER.debug(streamMarker, "Sent stream metadata action at {}: {}", new Object[]{m + l, streamData});
			}
		}
	}

	@Override
	public void method_10470(StreamMetadata streamData, long l, long m) {
		if (this.isLive() && this.streamSendMetadata) {
			long n = this.streamController.getStreamTime();
			String string = streamData.getDescription();
			String string2 = streamData.toJson();
			long o = this.streamController.method_10437(streamData.getName(), n + l, string, string2);
			if (o < 0L) {
				LOGGER.warn(streamMarker, "Could not send stream metadata sequence from {} to {}: {}", new Object[]{n + l, n + m, streamData});
			} else if (this.streamController.method_10436(streamData.getName(), n + m, o, string, string2)) {
				LOGGER.debug(streamMarker, "Sent stream metadata sequence from {} to {}: {}", new Object[]{n + l, n + m, streamData});
			} else {
				LOGGER.warn(streamMarker, "Half-sent stream metadata sequence from {} to {}: {}", new Object[]{n + l, n + m, streamData});
			}
		}
	}

	@Override
	public boolean isPaused() {
		return this.streamController.isPaused();
	}

	@Override
	public void requestCommercial() {
		if (this.streamController.requestCommercial()) {
			LOGGER.debug(streamMarker, "Requested commercial from Twitch");
		} else {
			LOGGER.warn(streamMarker, "Could not request commercial from Twitch");
		}
	}

	@Override
	public void pauseBroadcast() {
		this.streamController.pauseBroadcast();
		this.paused = true;
		this.setStreamVolume();
	}

	@Override
	public void resumeBroadcast() {
		this.streamController.resumeBroadcast();
		this.paused = false;
		this.setStreamVolume();
	}

	@Override
	public void setStreamVolume() {
		if (this.isLive()) {
			float f = this.client.options.streamSystemVolume;
			boolean bl = this.paused || f <= 0.0F;
			this.streamController.setPlaybackVolume(bl ? 0.0F : f);
			this.streamController.setRecorderVolume(this.isMuted() ? 0.0F : this.client.options.streamMicVolume);
		}
	}

	@Override
	public void initializeStreamProperties() {
		GameOptions gameOptions = this.client.options;
		VideoParams videoParams = this.streamController
			.setProperties(
				setStreamBitrate(gameOptions.streamKbps),
				setStreamFps(gameOptions.streamFps),
				setStreamBytesPerPixel(gameOptions.streamBytesPerPixel),
				(float)this.client.width / (float)this.client.height
			);
		switch (gameOptions.streamCompression) {
			case 0:
				videoParams.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_LOW;
				break;
			case 1:
				videoParams.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_MEDIUM;
				break;
			case 2:
				videoParams.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
		}

		if (this.framebuffer == null) {
			this.framebuffer = new Framebuffer(videoParams.outputWidth, videoParams.outputHeight, false);
		} else {
			this.framebuffer.resize(videoParams.outputWidth, videoParams.outputHeight);
		}

		if (gameOptions.currentTexturePackName != null && gameOptions.currentTexturePackName.length() > 0) {
			for (IngestServer ingestServer : this.getIngestServers()) {
				if (ingestServer.serverUrl.equals(gameOptions.currentTexturePackName)) {
					this.streamController.setIngestServer(ingestServer);
					break;
				}
			}
		}

		this.targetFps = videoParams.targetFps;
		this.streamSendMetadata = gameOptions.streamSendMetadata;
		this.streamController.setVideoParams(videoParams);
		LOGGER.info(
			streamMarker,
			"Streaming at {}/{} at {} kbps to {}",
			new Object[]{videoParams.outputWidth, videoParams.outputHeight, videoParams.maxKbps, this.streamController.getIngestServer().serverUrl}
		);
		this.streamController.setStreamInfo(null, "Minecraft", null);
	}

	@Override
	public void stopStream() {
		if (this.streamController.stopBroadcast()) {
			LOGGER.info(streamMarker, "Stopped streaming to Twitch");
		} else {
			LOGGER.warn(streamMarker, "Could not stop streaming to Twitch");
		}
	}

	@Override
	public void method_7192(ErrorCode code, AuthToken token) {
	}

	@Override
	public void logIn(ErrorCode code) {
		if (ErrorCode.succeeded(code)) {
			LOGGER.debug(streamMarker, "Login attempt successful");
			this.loggedIn = true;
		} else {
			LOGGER.warn(streamMarker, "Login attempt unsuccessful: {} (error code {})", new Object[]{ErrorCode.getString(code), code.getValue()});
			this.loggedIn = false;
		}
	}

	@Override
	public void method_7193(ErrorCode code, GameInfo[] infoList) {
	}

	@Override
	public void method_7190(StreamController.Status status) {
		LOGGER.debug(streamMarker, "Broadcast state changed to {}", new Object[]{status});
		if (status == StreamController.Status.INITIALIZED) {
			this.streamController.setState(StreamController.Status.AUTHENTICATED);
		}
	}

	@Override
	public void logOut() {
		LOGGER.info(streamMarker, "Logged out of twitch");
	}

	@Override
	public void setStreamInfo(StreamInfo info) {
		LOGGER.debug(streamMarker, "Stream info updated; {} viewers on stream ID {}", new Object[]{info.viewers, info.streamId});
	}

	@Override
	public void setIngestList(IngestList list) {
	}

	@Override
	public void warnFrameSubmission(ErrorCode code) {
		LOGGER.warn(streamMarker, "Issue submitting frame: {} (Error code {})", new Object[]{ErrorCode.getString(code), code.getValue()});
		this.client.inGameHud.getChatHud().addMessage(new LiteralText("Issue streaming frame: " + code + " (" + ErrorCode.getString(code) + ")"), 2);
	}

	@Override
	public void startBroadcast() {
		this.setStreamVolume();
		LOGGER.info(streamMarker, "Broadcast to Twitch has started");
	}

	@Override
	public void stopBroadcast() {
		LOGGER.info(streamMarker, "Broadcast to Twitch has stopped");
	}

	@Override
	public void method_7199(ErrorCode code) {
		if (code == ErrorCode.TTV_EC_SOUNDFLOWER_NOT_INSTALLED) {
			Text text = new TranslatableText("stream.unavailable.soundflower.chat.link");
			text.getStyle()
				.setClickEvent(
					new ClickEvent(
						ClickEvent.Action.OPEN_URL, "https://help.mojang.com/customer/portal/articles/1374877-configuring-soundflower-for-streaming-on-apple-computers"
					)
				);
			text.getStyle().setUnderline(true);
			Text text2 = new TranslatableText("stream.unavailable.soundflower.chat", text);
			text2.getStyle().setFormatting(Formatting.DARK_RED);
			this.client.inGameHud.getChatHud().addMessage(text2);
		} else {
			Text text3 = new TranslatableText("stream.unavailable.unknown.chat", ErrorCode.getString(code));
			text3.getStyle().setFormatting(Formatting.DARK_RED);
			this.client.inGameHud.getChatHud().addMessage(text3);
		}
	}

	@Override
	public void updateStreamState(TwitchStream stream, TwitchStream.Type streamType) {
		LOGGER.debug(streamMarker, "Ingest test state changed to {}", new Object[]{streamType});
		if (streamType == TwitchStream.Type.FINISHED) {
			this.streamFinished = true;
		}
	}

	public static int setStreamFps(float f) {
		return MathHelper.floor(10.0F + f * 50.0F);
	}

	public static int setStreamBitrate(float f) {
		return MathHelper.floor(230.0F + f * 3270.0F);
	}

	public static float setStreamBytesPerPixel(float f) {
		return 0.1F + f * 0.1F;
	}

	@Override
	public IngestServer[] getIngestServers() {
		return this.streamController.getIngestList().getServers();
	}

	@Override
	public void setStreamListener() {
		TwitchStream twitchStream = this.streamController.getStream();
		if (twitchStream != null) {
			twitchStream.setStreamListener(this);
		}
	}

	@Override
	public TwitchStream getTwitchStream() {
		return this.streamController.getTwitchStream();
	}

	@Override
	public boolean isTesting() {
		return this.streamController.isTesting();
	}

	@Override
	public int getViewerCount() {
		return this.isLive() ? this.streamController.getStreamInfo().viewers : 0;
	}

	@Override
	public void logChatStart(ErrorCode code) {
		if (ErrorCode.failed(code)) {
			LOGGER.error(streamMarker, "Chat failed to initialize");
		}
	}

	@Override
	public void logChatStop(ErrorCode code) {
		if (ErrorCode.failed(code)) {
			LOGGER.error(streamMarker, "Chat failed to shutdown");
		}
	}

	@Override
	public void setState(Twitch.StreamState state) {
	}

	@Override
	public void method_10461(String string, ChatRawMessage[] messages) {
		for (ChatRawMessage chatRawMessage : messages) {
			this.updateChatUser(chatRawMessage.userName, chatRawMessage);
			if (this.method_10472(chatRawMessage.modes, chatRawMessage.subscriptions, this.client.options.streamChatUserFilter)) {
				Text text = new LiteralText(chatRawMessage.userName);
				Text text2 = new TranslatableText(
					"chat.stream." + (chatRawMessage.action ? "emote" : "text"), this.twitchText, text, Formatting.strip(chatRawMessage.message)
				);
				if (chatRawMessage.action) {
					text2.getStyle().setItalic(true);
				}

				Text text3 = new LiteralText("");
				text3.append(new TranslatableText("stream.userinfo.chatTooltip"));

				for (Text text4 : StreamUtilitiesScreen.getDisplayTexts(chatRawMessage.modes, chatRawMessage.subscriptions, null)) {
					text3.append("\n");
					text3.append(text4);
				}

				text.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text3));
				text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.TWITCH_USER_INFO, chatRawMessage.userName));
				this.client.inGameHud.getChatHud().addMessage(text2);
			}
		}
	}

	@Override
	public void method_10462(String string, ChatTokenizedMessage[] messages) {
	}

	private void updateChatUser(String username, ChatRawMessage chatMessage) {
		ChatUserInfo chatUserInfo = (ChatUserInfo)this.twitchChat.get(username);
		if (chatUserInfo == null) {
			chatUserInfo = new ChatUserInfo();
			chatUserInfo.displayName = username;
			this.twitchChat.put(username, chatUserInfo);
		}

		chatUserInfo.subscriptions = chatMessage.subscriptions;
		chatUserInfo.modes = chatMessage.modes;
		chatUserInfo.nameColorARGB = chatMessage.nameColorARGB;
	}

	private boolean method_10472(Set<ChatUserMode> chatUserModes, Set<ChatUserSubscription> chatUserSubscriptions, int streamChatUserFilter) {
		if (chatUserModes.contains(ChatUserMode.TTV_CHAT_USERMODE_BANNED)) {
			return false;
		} else if (chatUserModes.contains(ChatUserMode.TTV_CHAT_USERMODE_ADMINSTRATOR)) {
			return true;
		} else if (chatUserModes.contains(ChatUserMode.TTV_CHAT_USERMODE_MODERATOR)) {
			return true;
		} else if (chatUserModes.contains(ChatUserMode.TTV_CHAT_USERMODE_STAFF)) {
			return true;
		} else if (streamChatUserFilter == 0) {
			return true;
		} else {
			return streamChatUserFilter == 1 ? chatUserSubscriptions.contains(ChatUserSubscription.TTV_CHAT_USERSUB_SUBSCRIBER) : false;
		}
	}

	@Override
	public void method_7220(String string, ChatUserInfo[] chatUserInfos, ChatUserInfo[] chatUserInfos2, ChatUserInfo[] chatUserInfos3) {
		for (ChatUserInfo chatUserInfo : chatUserInfos2) {
			this.twitchChat.remove(chatUserInfo.displayName);
		}

		for (ChatUserInfo chatUserInfo2 : chatUserInfos3) {
			this.twitchChat.put(chatUserInfo2.displayName, chatUserInfo2);
		}

		for (ChatUserInfo chatUserInfo3 : chatUserInfos) {
			this.twitchChat.put(chatUserInfo3.displayName, chatUserInfo3);
		}
	}

	@Override
	public void connectChat(String id) {
		LOGGER.debug(streamMarker, "Chat connected");
	}

	@Override
	public void disconnectChat(String id) {
		LOGGER.debug(streamMarker, "Chat disconnected");
		this.twitchChat.clear();
	}

	@Override
	public void method_10460(String string, String string2) {
	}

	@Override
	public void method_10465() {
	}

	@Override
	public void method_10468() {
	}

	@Override
	public void method_10464(String string) {
	}

	@Override
	public void method_10466(String string) {
	}

	@Override
	public boolean isChannelNameSet() {
		return this.currentChannelName != null && this.currentChannelName.equals(this.streamController.getChannelInfo().name);
	}

	@Override
	public String getCurrentChannelName() {
		return this.currentChannelName;
	}

	@Override
	public ChatUserInfo getUserInfo(String username) {
		return (ChatUserInfo)this.twitchChat.get(username);
	}

	@Override
	public void sendChatMessage(String chatMessage) {
		this.twitch.sendChatMessage(this.currentChannelName, chatMessage);
	}

	@Override
	public boolean isRunning() {
		return initialized && this.streamController.isRunning();
	}

	@Override
	public ErrorCode getErrorCode() {
		return !initialized ? ErrorCode.TTV_EC_OS_TOO_OLD : this.streamController.getErrorCode();
	}

	@Override
	public boolean isLoggedIn() {
		return this.loggedIn;
	}

	@Override
	public void toggleMic(boolean micEnabled) {
		this.micEnabled = micEnabled;
		this.setStreamVolume();
	}

	@Override
	public boolean isMuted() {
		boolean bl = this.client.options.streamMicToggleBehavior == 1;
		return this.paused || this.client.options.streamMicVolume <= 0.0F || bl != this.micEnabled;
	}

	@Override
	public TwitchStreamProvider.Reason getReason() {
		return this.reason;
	}

	static {
		try {
			if (Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS) {
				System.loadLibrary("avutil-ttv-51");
				System.loadLibrary("swresample-ttv-0");
				System.loadLibrary("libmp3lame-ttv");
				if (System.getProperty("os.arch").contains("64")) {
					System.loadLibrary("libmfxsw64");
				} else {
					System.loadLibrary("libmfxsw32");
				}
			}

			initialized = true;
		} catch (Throwable var1) {
			initialized = false;
		}
	}
}
