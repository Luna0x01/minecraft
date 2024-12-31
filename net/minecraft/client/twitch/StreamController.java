package net.minecraft.client.twitch;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.ThreadSafeBound;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.twitch.AuthToken;
import tv.twitch.Core;
import tv.twitch.ErrorCode;
import tv.twitch.MessageLevel;
import tv.twitch.StandardCoreAPI;
import tv.twitch.broadcast.ArchivingState;
import tv.twitch.broadcast.AudioDeviceType;
import tv.twitch.broadcast.AudioParams;
import tv.twitch.broadcast.ChannelInfo;
import tv.twitch.broadcast.DesktopStreamAPI;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfo;
import tv.twitch.broadcast.GameInfoList;
import tv.twitch.broadcast.IStatCallbacks;
import tv.twitch.broadcast.IStreamCallbacks;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.PixelFormat;
import tv.twitch.broadcast.StartFlags;
import tv.twitch.broadcast.StatType;
import tv.twitch.broadcast.Stream;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.StreamInfoForSetting;
import tv.twitch.broadcast.UserInfo;
import tv.twitch.broadcast.VideoParams;

public class StreamController {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final int field_8236 = 30;
	protected final int field_8237 = 3;
	private static final ThreadSafeBound<String> THREAD_SAFE_BOUND = new ThreadSafeBound<>(String.class, 50);
	private String message = null;
	protected StreamController.Listener listener = null;
	protected String clientId = "";
	protected String field_8240 = "";
	protected String field_8241 = "";
	protected boolean field_8242 = true;
	protected Core core = null;
	protected Stream stream = null;
	protected List<FrameBuffer> field_8245 = Lists.newArrayList();
	protected List<FrameBuffer> field_8246 = Lists.newArrayList();
	protected boolean running = false;
	protected boolean loginSuccessful = false;
	protected boolean shuttingDown = false;
	protected StreamController.Status status = StreamController.Status.UNINITIALIZED;
	protected String username = null;
	protected VideoParams videoParams = null;
	protected AudioParams audioParams = null;
	protected IngestList ingestList = new IngestList(new IngestServer[0]);
	protected IngestServer ingestServer = null;
	protected AuthToken authToken = new AuthToken();
	protected ChannelInfo channelInfo = new ChannelInfo();
	protected UserInfo userInfo = new UserInfo();
	protected StreamInfo streamInfo = new StreamInfo();
	protected ArchivingState archivingState = new ArchivingState();
	protected long field_8261 = 0L;
	protected TwitchStream twitchStream = null;
	private ErrorCode errorCode;
	protected IStreamCallbacks streamCallbacks = new IStreamCallbacks() {
		public void requestAuthTokenCallback(ErrorCode code, AuthToken token) {
			if (ErrorCode.succeeded(code)) {
				StreamController.this.authToken = token;
				StreamController.this.setState(StreamController.Status.AUTHENTICATED);
			} else {
				StreamController.this.authToken.data = "";
				StreamController.this.setState(StreamController.Status.INITIALIZED);
				String string = ErrorCode.getString(code);
				StreamController.this.sendErrorMessage(String.format("RequestAuthTokenDoneCallback got failure: %s", string));
			}

			try {
				if (StreamController.this.listener != null) {
					StreamController.this.listener.method_7192(code, token);
				}
			} catch (Exception var4) {
				StreamController.this.sendErrorMessage(var4.toString());
			}
		}

		public void loginCallback(ErrorCode code, ChannelInfo info) {
			if (ErrorCode.succeeded(code)) {
				StreamController.this.channelInfo = info;
				StreamController.this.setState(StreamController.Status.LOGGED_IN);
				StreamController.this.loginSuccessful = true;
			} else {
				StreamController.this.setState(StreamController.Status.INITIALIZED);
				StreamController.this.loginSuccessful = false;
				String string = ErrorCode.getString(code);
				StreamController.this.sendErrorMessage(String.format("LoginCallback got failure: %s", string));
			}

			try {
				if (StreamController.this.listener != null) {
					StreamController.this.listener.logIn(code);
				}
			} catch (Exception var4) {
				StreamController.this.sendErrorMessage(var4.toString());
			}
		}

		public void getIngestServersCallback(ErrorCode code, IngestList list) {
			if (ErrorCode.succeeded(code)) {
				StreamController.this.ingestList = list;
				StreamController.this.ingestServer = StreamController.this.ingestList.getDefaultServer();
				StreamController.this.setState(StreamController.Status.RECEIVED_INGEST_SERVERS);

				try {
					if (StreamController.this.listener != null) {
						StreamController.this.listener.setIngestList(list);
					}
				} catch (Exception var4) {
					StreamController.this.sendErrorMessage(var4.toString());
				}
			} else {
				String string = ErrorCode.getString(code);
				StreamController.this.sendErrorMessage(String.format("IngestListCallback got failure: %s", string));
				StreamController.this.setState(StreamController.Status.LOGGING_IN);
			}
		}

		public void getUserInfoCallback(ErrorCode code, UserInfo info) {
			StreamController.this.userInfo = info;
			if (ErrorCode.failed(code)) {
				String string = ErrorCode.getString(code);
				StreamController.this.sendErrorMessage(String.format("UserInfoDoneCallback got failure: %s", string));
			}
		}

		public void getStreamInfoCallback(ErrorCode code, StreamInfo info) {
			if (ErrorCode.succeeded(code)) {
				StreamController.this.streamInfo = info;

				try {
					if (StreamController.this.listener != null) {
						StreamController.this.listener.setStreamInfo(info);
					}
				} catch (Exception var4) {
					StreamController.this.sendErrorMessage(var4.toString());
				}
			} else {
				String string = ErrorCode.getString(code);
				StreamController.this.sendWarnMessage(String.format("StreamInfoDoneCallback got failure: %s", string));
			}
		}

		public void getArchivingStateCallback(ErrorCode code, ArchivingState state) {
			StreamController.this.archivingState = state;
			if (ErrorCode.failed(code)) {
			}
		}

		public void runCommercialCallback(ErrorCode code) {
			if (ErrorCode.failed(code)) {
				String string = ErrorCode.getString(code);
				StreamController.this.sendWarnMessage(String.format("RunCommercialCallback got failure: %s", string));
			}
		}

		public void setStreamInfoCallback(ErrorCode code) {
			if (ErrorCode.failed(code)) {
				String string = ErrorCode.getString(code);
				StreamController.this.sendWarnMessage(String.format("SetStreamInfoCallback got failure: %s", string));
			}
		}

		public void getGameNameListCallback(ErrorCode code, GameInfoList gameInfoList) {
			if (ErrorCode.failed(code)) {
				String string = ErrorCode.getString(code);
				StreamController.this.sendErrorMessage(String.format("GameNameListCallback got failure: %s", string));
			}

			try {
				if (StreamController.this.listener != null) {
					StreamController.this.listener.method_7193(code, gameInfoList == null ? new GameInfo[0] : gameInfoList.list);
				}
			} catch (Exception var4) {
				StreamController.this.sendErrorMessage(var4.toString());
			}
		}

		public void bufferUnlockCallback(long l) {
			FrameBuffer frameBuffer = FrameBuffer.lookupBuffer(l);
			StreamController.this.field_8246.add(frameBuffer);
		}

		public void startCallback(ErrorCode code) {
			if (ErrorCode.succeeded(code)) {
				try {
					if (StreamController.this.listener != null) {
						StreamController.this.listener.startBroadcast();
					}
				} catch (Exception var4) {
					StreamController.this.sendErrorMessage(var4.toString());
				}

				StreamController.this.setState(StreamController.Status.BROADCASTING);
			} else {
				StreamController.this.videoParams = null;
				StreamController.this.audioParams = null;
				StreamController.this.setState(StreamController.Status.READY_TO_BROADCAST);

				try {
					if (StreamController.this.listener != null) {
						StreamController.this.listener.method_7199(code);
					}
				} catch (Exception var3) {
					StreamController.this.sendErrorMessage(var3.toString());
				}

				String string = ErrorCode.getString(code);
				StreamController.this.sendErrorMessage(String.format("startCallback got failure: %s", string));
			}
		}

		public void stopCallback(ErrorCode code) {
			if (ErrorCode.succeeded(code)) {
				StreamController.this.videoParams = null;
				StreamController.this.audioParams = null;
				StreamController.this.method_7156();

				try {
					if (StreamController.this.listener != null) {
						StreamController.this.listener.stopBroadcast();
					}
				} catch (Exception var3) {
					StreamController.this.sendErrorMessage(var3.toString());
				}

				if (StreamController.this.loginSuccessful) {
					StreamController.this.setState(StreamController.Status.READY_TO_BROADCAST);
				} else {
					StreamController.this.setState(StreamController.Status.INITIALIZED);
				}
			} else {
				StreamController.this.setState(StreamController.Status.READY_TO_BROADCAST);
				String string = ErrorCode.getString(code);
				StreamController.this.sendErrorMessage(String.format("stopCallback got failure: %s", string));
			}
		}

		public void sendActionMetaDataCallback(ErrorCode code) {
			if (ErrorCode.failed(code)) {
				String string = ErrorCode.getString(code);
				StreamController.this.sendErrorMessage(String.format("sendActionMetaDataCallback got failure: %s", string));
			}
		}

		public void sendStartSpanMetaDataCallback(ErrorCode code) {
			if (ErrorCode.failed(code)) {
				String string = ErrorCode.getString(code);
				StreamController.this.sendErrorMessage(String.format("sendStartSpanMetaDataCallback got failure: %s", string));
			}
		}

		public void sendEndSpanMetaDataCallback(ErrorCode code) {
			if (ErrorCode.failed(code)) {
				String string = ErrorCode.getString(code);
				StreamController.this.sendErrorMessage(String.format("sendEndSpanMetaDataCallback got failure: %s", string));
			}
		}
	};
	protected IStatCallbacks statCallbacks = new IStatCallbacks() {
		public void statCallback(StatType statType, long l) {
		}
	};

	public void setListener(StreamController.Listener listener) {
		this.listener = listener;
	}

	public boolean isRunning() {
		return this.running;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public StreamInfo getStreamInfo() {
		return this.streamInfo;
	}

	public ChannelInfo getChannelInfo() {
		return this.channelInfo;
	}

	public boolean isLive() {
		return this.status == StreamController.Status.BROADCASTING || this.status == StreamController.Status.PAUSED;
	}

	public boolean isReady() {
		return this.status == StreamController.Status.READY_TO_BROADCAST;
	}

	public boolean isTesting() {
		return this.status == StreamController.Status.INGEST_TESTING;
	}

	public boolean isPaused() {
		return this.status == StreamController.Status.PAUSED;
	}

	public boolean isLoginSuccessful() {
		return this.loginSuccessful;
	}

	public IngestServer getIngestServer() {
		return this.ingestServer;
	}

	public void setIngestServer(IngestServer ingestServer) {
		this.ingestServer = ingestServer;
	}

	public IngestList getIngestList() {
		return this.ingestList;
	}

	public void setRecorderVolume(float volume) {
		this.stream.setVolume(AudioDeviceType.TTV_RECORDER_DEVICE, volume);
	}

	public void setPlaybackVolume(float volume) {
		this.stream.setVolume(AudioDeviceType.TTV_PLAYBACK_DEVICE, volume);
	}

	public TwitchStream getTwitchStream() {
		return this.twitchStream;
	}

	public long getStreamTime() {
		return this.stream.getStreamTime();
	}

	protected boolean method_7187() {
		return true;
	}

	public ErrorCode getErrorCode() {
		return this.errorCode;
	}

	public StreamController() {
		this.core = Core.getInstance();
		if (Core.getInstance() == null) {
			this.core = new Core(new StandardCoreAPI());
		}

		this.stream = new Stream(new DesktopStreamAPI());
	}

	protected PixelFormat getPixelFormat() {
		return PixelFormat.TTV_PF_RGBA;
	}

	public boolean start() {
		if (this.running) {
			return false;
		} else {
			this.stream.setStreamCallbacks(this.streamCallbacks);
			ErrorCode errorCode = this.core.initialize(this.clientId, System.getProperty("java.library.path"));
			if (!this.wasSuccessful(errorCode)) {
				this.stream.setStreamCallbacks(null);
				this.errorCode = errorCode;
				return false;
			} else {
				errorCode = this.core.setTraceLevel(MessageLevel.TTV_ML_ERROR);
				if (!this.wasSuccessful(errorCode)) {
					this.stream.setStreamCallbacks(null);
					this.core.shutdown();
					this.errorCode = errorCode;
					return false;
				} else if (ErrorCode.succeeded(errorCode)) {
					this.running = true;
					this.setState(StreamController.Status.INITIALIZED);
					return true;
				} else {
					this.errorCode = errorCode;
					this.core.shutdown();
					return false;
				}
			}
		}
	}

	public boolean shutdown() {
		if (!this.running) {
			return true;
		} else if (this.isTesting()) {
			return false;
		} else {
			this.shuttingDown = true;
			this.stopStream();
			this.stream.setStreamCallbacks(null);
			this.stream.setStatCallbacks(null);
			ErrorCode errorCode = this.core.shutdown();
			this.wasSuccessful(errorCode);
			this.running = false;
			this.shuttingDown = false;
			this.setState(StreamController.Status.UNINITIALIZED);
			return true;
		}
	}

	public void method_10435() {
		if (this.status != StreamController.Status.UNINITIALIZED) {
			if (this.twitchStream != null) {
				this.twitchStream.shutdown();
			}

			for (; this.twitchStream != null; this.method_7152()) {
				try {
					Thread.sleep(200L);
				} catch (Exception var2) {
					this.sendErrorMessage(var2.toString());
				}
			}

			this.shutdown();
		}
	}

	public boolean setAuthentication(String username, AuthToken authToken) {
		if (this.isTesting()) {
			return false;
		} else {
			this.stopStream();
			if (username == null || username.isEmpty()) {
				this.sendErrorMessage("Username must be valid");
				return false;
			} else if (authToken != null && authToken.data != null && !authToken.data.isEmpty()) {
				this.username = username;
				this.authToken = authToken;
				if (this.isRunning()) {
					this.setState(StreamController.Status.AUTHENTICATED);
				}

				return true;
			} else {
				this.sendErrorMessage("Auth token must be valid");
				return false;
			}
		}
	}

	public boolean stopStream() {
		if (this.isTesting()) {
			return false;
		} else {
			if (this.isLive()) {
				this.stream.stop(false);
			}

			this.username = "";
			this.authToken = new AuthToken();
			if (!this.loginSuccessful) {
				return false;
			} else {
				this.loginSuccessful = false;
				if (!this.shuttingDown) {
					try {
						if (this.listener != null) {
							this.listener.logOut();
						}
					} catch (Exception var2) {
						this.sendErrorMessage(var2.toString());
					}
				}

				this.setState(StreamController.Status.INITIALIZED);
				return true;
			}
		}
	}

	public boolean setStreamInfo(String username, String gameName, String streamTitle) {
		if (!this.loginSuccessful) {
			return false;
		} else {
			if (username == null || username.equals("")) {
				username = this.username;
			}

			if (gameName == null) {
				gameName = "";
			}

			if (streamTitle == null) {
				streamTitle = "";
			}

			StreamInfoForSetting streamInfoForSetting = new StreamInfoForSetting();
			streamInfoForSetting.streamTitle = streamTitle;
			streamInfoForSetting.gameName = gameName;
			ErrorCode errorCode = this.stream.setStreamInfo(this.authToken, username, streamInfoForSetting);
			this.wasSuccessful(errorCode);
			return ErrorCode.succeeded(errorCode);
		}
	}

	public boolean requestCommercial() {
		if (!this.isLive()) {
			return false;
		} else {
			ErrorCode errorCode = this.stream.runCommercial(this.authToken);
			this.wasSuccessful(errorCode);
			return ErrorCode.succeeded(errorCode);
		}
	}

	public VideoParams setProperties(int maxKbps, int fps, float bytesPerPixel, float aspectRatio) {
		int[] is = this.stream.getMaxResolution(maxKbps, fps, bytesPerPixel, aspectRatio);
		VideoParams videoParams = new VideoParams();
		videoParams.maxKbps = maxKbps;
		videoParams.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
		videoParams.pixelFormat = this.getPixelFormat();
		videoParams.targetFps = fps;
		videoParams.outputWidth = is[0];
		videoParams.outputHeight = is[1];
		videoParams.disableAdaptiveBitrate = false;
		videoParams.verticalFlip = false;
		return videoParams;
	}

	public boolean setVideoParams(VideoParams videoParams) {
		if (videoParams != null && this.isReady()) {
			this.videoParams = videoParams.clone();
			this.audioParams = new AudioParams();
			this.audioParams.audioEnabled = this.field_8242 && this.method_7187();
			this.audioParams.enableMicCapture = this.audioParams.audioEnabled;
			this.audioParams.enablePlaybackCapture = this.audioParams.audioEnabled;
			this.audioParams.enablePassthroughAudio = false;
			if (!this.method_7155()) {
				this.videoParams = null;
				this.audioParams = null;
				return false;
			} else {
				ErrorCode errorCode = this.stream.start(videoParams, this.audioParams, this.ingestServer, StartFlags.None, true);
				if (ErrorCode.failed(errorCode)) {
					this.method_7156();
					String string = ErrorCode.getString(errorCode);
					this.sendErrorMessage(String.format("Error while starting to broadcast: %s", string));
					this.videoParams = null;
					this.audioParams = null;
					return false;
				} else {
					this.setState(StreamController.Status.STARTING);
					return true;
				}
			}
		} else {
			return false;
		}
	}

	public boolean stopBroadcast() {
		if (!this.isLive()) {
			return false;
		} else {
			ErrorCode errorCode = this.stream.stop(true);
			if (ErrorCode.failed(errorCode)) {
				String string = ErrorCode.getString(errorCode);
				this.sendErrorMessage(String.format("Error while stopping the broadcast: %s", string));
				return false;
			} else {
				this.setState(StreamController.Status.STOPPING);
				return ErrorCode.succeeded(errorCode);
			}
		}
	}

	public boolean pauseBroadcast() {
		if (!this.isLive()) {
			return false;
		} else {
			ErrorCode errorCode = this.stream.pauseVideo();
			if (ErrorCode.failed(errorCode)) {
				this.stopBroadcast();
				String string = ErrorCode.getString(errorCode);
				this.sendErrorMessage(String.format("Error pausing stream: %s\n", string));
			} else {
				this.setState(StreamController.Status.PAUSED);
			}

			return ErrorCode.succeeded(errorCode);
		}
	}

	public boolean resumeBroadcast() {
		if (!this.isPaused()) {
			return false;
		} else {
			this.setState(StreamController.Status.BROADCASTING);
			return true;
		}
	}

	public boolean method_7164(String string, long l, String string2, String string3) {
		ErrorCode errorCode = this.stream.sendActionMetaData(this.authToken, string, l, string2, string3);
		if (ErrorCode.failed(errorCode)) {
			String string4 = ErrorCode.getString(errorCode);
			this.sendErrorMessage(String.format("Error while sending meta data: %s\n", string4));
			return false;
		} else {
			return true;
		}
	}

	public long method_10437(String string, long l, String string2, String string3) {
		long m = this.stream.sendStartSpanMetaData(this.authToken, string, l, string2, string3);
		if (m == -1L) {
			this.sendErrorMessage(String.format("Error in SendStartSpanMetaData\n"));
		}

		return m;
	}

	public boolean method_10436(String string, long l, long m, String string2, String string3) {
		if (m == -1L) {
			this.sendErrorMessage(String.format("Invalid sequence id: %d\n", m));
			return false;
		} else {
			ErrorCode errorCode = this.stream.sendEndSpanMetaData(this.authToken, string, l, m, string2, string3);
			if (ErrorCode.failed(errorCode)) {
				String string4 = ErrorCode.getString(errorCode);
				this.sendErrorMessage(String.format("Error in SendStopSpanMetaData: %s\n", string4));
				return false;
			} else {
				return true;
			}
		}
	}

	protected void setState(StreamController.Status status) {
		if (status != this.status) {
			this.status = status;

			try {
				if (this.listener != null) {
					this.listener.method_7190(status);
				}
			} catch (Exception var3) {
				this.sendErrorMessage(var3.toString());
			}
		}
	}

	public void method_7152() {
		if (this.stream != null && this.running) {
			ErrorCode errorCode = this.stream.pollTasks();
			this.wasSuccessful(errorCode);
			if (this.isTesting()) {
				this.twitchStream.method_7233();
				if (this.twitchStream.isNotBroadcasting()) {
					this.twitchStream = null;
					this.setState(StreamController.Status.READY_TO_BROADCAST);
				}
			}

			switch (this.status) {
				case AUTHENTICATED:
					this.setState(StreamController.Status.LOGGING_IN);
					errorCode = this.stream.login(this.authToken);
					if (ErrorCode.failed(errorCode)) {
						String string = ErrorCode.getString(errorCode);
						this.sendErrorMessage(String.format("Error in TTV_Login: %s\n", string));
					}
					break;
				case LOGGED_IN:
					this.setState(StreamController.Status.FINDING_INGEST_SERVER);
					errorCode = this.stream.getIngestServers(this.authToken);
					if (ErrorCode.failed(errorCode)) {
						this.setState(StreamController.Status.LOGGED_IN);
						String string2 = ErrorCode.getString(errorCode);
						this.sendErrorMessage(String.format("Error in TTV_GetIngestServers: %s\n", string2));
					}
					break;
				case RECEIVED_INGEST_SERVERS:
					this.setState(StreamController.Status.READY_TO_BROADCAST);
					errorCode = this.stream.getUserInfo(this.authToken);
					if (ErrorCode.failed(errorCode)) {
						String string3 = ErrorCode.getString(errorCode);
						this.sendErrorMessage(String.format("Error in TTV_GetUserInfo: %s\n", string3));
					}

					this.method_7153();
					errorCode = this.stream.getArchivingState(this.authToken);
					if (ErrorCode.failed(errorCode)) {
						String string4 = ErrorCode.getString(errorCode);
						this.sendErrorMessage(String.format("Error in TTV_GetArchivingState: %s\n", string4));
					}
				case STARTING:
				case STOPPING:
				case FINDING_INGEST_SERVER:
				case AUTHENTICATING:
				case INITIALIZED:
				case UNINITIALIZED:
				case INGEST_TESTING:
				default:
					break;
				case PAUSED:
				case BROADCASTING:
					this.method_7153();
			}
		}
	}

	protected void method_7153() {
		long l = System.nanoTime();
		long m = (l - this.field_8261) / 1000000000L;
		if (m >= 30L) {
			this.field_8261 = l;
			ErrorCode errorCode = this.stream.getStreamInfo(this.authToken, this.username);
			if (ErrorCode.failed(errorCode)) {
				String string = ErrorCode.getString(errorCode);
				this.sendErrorMessage(String.format("Error in TTV_GetStreamInfo: %s", string));
			}
		}
	}

	public TwitchStream getStream() {
		if (!this.isReady() || this.ingestList == null) {
			return null;
		} else if (this.isTesting()) {
			return null;
		} else {
			this.twitchStream = new TwitchStream(this.stream, this.ingestList);
			this.twitchStream.init();
			this.setState(StreamController.Status.INGEST_TESTING);
			return this.twitchStream;
		}
	}

	protected boolean method_7155() {
		for (int i = 0; i < 3; i++) {
			FrameBuffer frameBuffer = this.stream.allocateFrameBuffer(this.videoParams.outputWidth * this.videoParams.outputHeight * 4);
			if (!frameBuffer.getIsValid()) {
				this.sendErrorMessage(String.format("Error while allocating frame buffer"));
				return false;
			}

			this.field_8245.add(frameBuffer);
			this.field_8246.add(frameBuffer);
		}

		return true;
	}

	protected void method_7156() {
		for (int i = 0; i < this.field_8245.size(); i++) {
			FrameBuffer frameBuffer = (FrameBuffer)this.field_8245.get(i);
			frameBuffer.free();
		}

		this.field_8246.clear();
		this.field_8245.clear();
	}

	public FrameBuffer method_7157() {
		if (this.field_8246.size() == 0) {
			this.sendErrorMessage(String.format("Out of free buffers, this should never happen"));
			return null;
		} else {
			FrameBuffer frameBuffer = (FrameBuffer)this.field_8246.get(this.field_8246.size() - 1);
			this.field_8246.remove(this.field_8246.size() - 1);
			return frameBuffer;
		}
	}

	public void method_7168(FrameBuffer frameBuffer) {
		try {
			this.stream.captureFrameBuffer_ReadPixels(frameBuffer);
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Trying to submit a frame to Twitch");
			CrashReportSection crashReportSection = crashReport.addElement("Broadcast State");
			crashReportSection.add("Last reported errors", Arrays.toString(THREAD_SAFE_BOUND.copyAndGetData()));
			crashReportSection.add("Buffer", frameBuffer);
			crashReportSection.add("Free buffer count", this.field_8246.size());
			crashReportSection.add("Capture buffer count", this.field_8245.size());
			throw new CrashException(crashReport);
		}
	}

	public ErrorCode method_7173(FrameBuffer buffer) {
		if (this.isPaused()) {
			this.resumeBroadcast();
		} else if (!this.isLive()) {
			return ErrorCode.TTV_EC_STREAM_NOT_STARTED;
		}

		ErrorCode errorCode = this.stream.submitVideoFrame(buffer);
		if (errorCode != ErrorCode.TTV_EC_SUCCESS) {
			String string = ErrorCode.getString(errorCode);
			if (ErrorCode.succeeded(errorCode)) {
				this.sendWarnMessage(String.format("Warning in SubmitTexturePointer: %s\n", string));
			} else {
				this.sendErrorMessage(String.format("Error in SubmitTexturePointer: %s\n", string));
				this.stopBroadcast();
			}

			if (this.listener != null) {
				this.listener.warnFrameSubmission(errorCode);
			}
		}

		return errorCode;
	}

	protected boolean wasSuccessful(ErrorCode code) {
		if (ErrorCode.failed(code)) {
			this.sendErrorMessage(ErrorCode.getString(code));
			return false;
		} else {
			return true;
		}
	}

	protected void sendErrorMessage(String message) {
		this.message = message;
		THREAD_SAFE_BOUND.add("<Error> " + message);
		LOGGER.error(TwitchAuth.streamMarker, "[Broadcast controller] {}", new Object[]{message});
	}

	protected void sendWarnMessage(String message) {
		THREAD_SAFE_BOUND.add("<Warning> " + message);
		LOGGER.warn(TwitchAuth.streamMarker, "[Broadcast controller] {}", new Object[]{message});
	}

	public interface Listener {
		void method_7192(ErrorCode code, AuthToken token);

		void logIn(ErrorCode code);

		void method_7193(ErrorCode code, GameInfo[] infoList);

		void method_7190(StreamController.Status status);

		void logOut();

		void setStreamInfo(StreamInfo info);

		void setIngestList(IngestList list);

		void warnFrameSubmission(ErrorCode code);

		void startBroadcast();

		void stopBroadcast();

		void method_7199(ErrorCode code);
	}

	public static enum Status {
		UNINITIALIZED,
		INITIALIZED,
		AUTHENTICATING,
		AUTHENTICATED,
		LOGGING_IN,
		LOGGED_IN,
		FINDING_INGEST_SERVER,
		RECEIVED_INGEST_SERVERS,
		READY_TO_BROADCAST,
		STARTING,
		BROADCASTING,
		STOPPING,
		PAUSED,
		INGEST_TESTING;
	}
}
