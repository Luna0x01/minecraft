package net.minecraft.client.twitch;

import com.google.common.collect.Lists;
import java.util.List;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.ArchivingState;
import tv.twitch.broadcast.AudioParams;
import tv.twitch.broadcast.ChannelInfo;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfoList;
import tv.twitch.broadcast.IStatCallbacks;
import tv.twitch.broadcast.IStreamCallbacks;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.PixelFormat;
import tv.twitch.broadcast.RTMPState;
import tv.twitch.broadcast.StartFlags;
import tv.twitch.broadcast.StatType;
import tv.twitch.broadcast.Stream;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.UserInfo;
import tv.twitch.broadcast.VideoParams;

public class TwitchStream {
	protected TwitchStream.Listener listener = null;
	protected Stream stream = null;
	protected IngestList ingestList = null;
	protected TwitchStream.Type streamType = TwitchStream.Type.UNINITIALIZED;
	protected long maxTestTime = 8000L;
	protected long field_8308 = 2000L;
	protected long field_8309 = 0L;
	protected RTMPState rtmpState = RTMPState.Invalid;
	protected VideoParams videoArgs = null;
	protected AudioParams audioArgs = null;
	protected long startTestTime = 0L;
	protected List<FrameBuffer> frameBuffer = null;
	protected boolean field_8315 = false;
	protected IStreamCallbacks field_8316 = null;
	protected IStatCallbacks field_8317 = null;
	protected IngestServer ingestServer = null;
	protected boolean isShutdown = false;
	protected boolean field_8320 = false;
	protected int availableServers = -1;
	protected int field_8322 = 0;
	protected long field_8323 = 0L;
	protected float field_8324 = 0.0F;
	protected float testCompletePercentage = 0.0F;
	protected boolean field_11384 = false;
	protected boolean field_11385 = false;
	protected boolean field_11386 = false;
	protected IStreamCallbacks streamCallbacks = new IStreamCallbacks() {
		public void requestAuthTokenCallback(ErrorCode code, AuthToken authToken) {
		}

		public void loginCallback(ErrorCode code, ChannelInfo channelInfo) {
		}

		public void getIngestServersCallback(ErrorCode code, IngestList ingestList) {
		}

		public void getUserInfoCallback(ErrorCode code, UserInfo userInfo) {
		}

		public void getStreamInfoCallback(ErrorCode code, StreamInfo streamInfo) {
		}

		public void getArchivingStateCallback(ErrorCode code, ArchivingState archivingState) {
		}

		public void runCommercialCallback(ErrorCode code) {
		}

		public void setStreamInfoCallback(ErrorCode code) {
		}

		public void getGameNameListCallback(ErrorCode code, GameInfoList gameInfoList) {
		}

		public void bufferUnlockCallback(long l) {
		}

		public void startCallback(ErrorCode code) {
			TwitchStream.this.field_11385 = false;
			if (ErrorCode.succeeded(code)) {
				TwitchStream.this.field_11384 = true;
				TwitchStream.this.startTestTime = System.currentTimeMillis();
				TwitchStream.this.setStreamType(TwitchStream.Type.CONNECTING);
			} else {
				TwitchStream.this.field_8315 = false;
				TwitchStream.this.setStreamType(TwitchStream.Type.FINISHED_TESTING);
			}
		}

		public void stopCallback(ErrorCode code) {
			if (ErrorCode.failed(code)) {
				System.out.println("IngestTester.stopCallback failed to stop - " + TwitchStream.this.ingestServer.serverName + ": " + code.toString());
			}

			TwitchStream.this.field_11386 = false;
			TwitchStream.this.field_11384 = false;
			TwitchStream.this.setStreamType(TwitchStream.Type.FINISHED_TESTING);
			TwitchStream.this.ingestServer = null;
			if (TwitchStream.this.isShutdown) {
				TwitchStream.this.setStreamType(TwitchStream.Type.CANCELLING);
			}
		}

		public void sendActionMetaDataCallback(ErrorCode code) {
		}

		public void sendStartSpanMetaDataCallback(ErrorCode code) {
		}

		public void sendEndSpanMetaDataCallback(ErrorCode code) {
		}
	};
	protected IStatCallbacks statCallbacks = new IStatCallbacks() {
		public void statCallback(StatType type, long l) {
			switch (type) {
				case TTV_ST_RTMPSTATE:
					TwitchStream.this.rtmpState = RTMPState.lookupValue((int)l);
					break;
				case TTV_ST_RTMPDATASENT:
					TwitchStream.this.field_8309 = l;
			}
		}
	};

	public void setStreamListener(TwitchStream.Listener listener) {
		this.listener = listener;
	}

	public IngestServer getIngestServer() {
		return this.ingestServer;
	}

	public int getAvailableServers() {
		return this.availableServers;
	}

	public boolean isNotBroadcasting() {
		return this.streamType == TwitchStream.Type.FINISHED || this.streamType == TwitchStream.Type.CANCELLED || this.streamType == TwitchStream.Type.FAILED;
	}

	public float getTestCompletePercentage() {
		return this.testCompletePercentage;
	}

	public TwitchStream(Stream stream, IngestList ingestList) {
		this.stream = stream;
		this.ingestList = ingestList;
	}

	public void init() {
		if (this.streamType == TwitchStream.Type.UNINITIALIZED) {
			this.availableServers = 0;
			this.isShutdown = false;
			this.field_8320 = false;
			this.field_11384 = false;
			this.field_11385 = false;
			this.field_11386 = false;
			this.field_8317 = this.stream.getStatCallbacks();
			this.stream.setStatCallbacks(this.statCallbacks);
			this.field_8316 = this.stream.getStreamCallbacks();
			this.stream.setStreamCallbacks(this.streamCallbacks);
			this.videoArgs = new VideoParams();
			this.videoArgs.targetFps = 60;
			this.videoArgs.maxKbps = 3500;
			this.videoArgs.outputWidth = 1280;
			this.videoArgs.outputHeight = 720;
			this.videoArgs.pixelFormat = PixelFormat.TTV_PF_BGRA;
			this.videoArgs.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
			this.videoArgs.disableAdaptiveBitrate = true;
			this.videoArgs.verticalFlip = false;
			this.stream.getDefaultParams(this.videoArgs);
			this.audioArgs = new AudioParams();
			this.audioArgs.audioEnabled = false;
			this.audioArgs.enableMicCapture = false;
			this.audioArgs.enablePlaybackCapture = false;
			this.audioArgs.enablePassthroughAudio = false;
			this.frameBuffer = Lists.newArrayList();
			int i = 3;

			for (int j = 0; j < i; j++) {
				FrameBuffer frameBuffer = this.stream.allocateFrameBuffer(this.videoArgs.outputWidth * this.videoArgs.outputHeight * 4);
				if (!frameBuffer.getIsValid()) {
					this.method_7237();
					this.setStreamType(TwitchStream.Type.FAILED);
					return;
				}

				this.frameBuffer.add(frameBuffer);
				this.stream.randomizeFrameBuffer(frameBuffer);
			}

			this.setStreamType(TwitchStream.Type.STARTING);
			this.startTestTime = System.currentTimeMillis();
		}
	}

	public void method_7233() {
		if (!this.isNotBroadcasting() && this.streamType != TwitchStream.Type.UNINITIALIZED) {
			if (!this.field_11385 && !this.field_11386) {
				switch (this.streamType) {
					case STARTING:
					case FINISHED_TESTING:
						if (this.ingestServer != null) {
							if (this.field_8320 || !this.field_8315) {
								this.ingestServer.bitrateKbps = 0.0F;
							}

							this.method_7227(this.ingestServer);
						} else {
							this.startTestTime = 0L;
							this.field_8320 = false;
							this.field_8315 = true;
							if (this.streamType != TwitchStream.Type.STARTING) {
								this.availableServers++;
							}

							if (this.availableServers < this.ingestList.getServers().length) {
								this.ingestServer = this.ingestList.getServers()[this.availableServers];
								this.method_7226(this.ingestServer);
							} else {
								this.setStreamType(TwitchStream.Type.FINISHED);
							}
						}
						break;
					case CONNECTING:
					case TESTING:
						this.method_7229(this.ingestServer);
						break;
					case CANCELLING:
						this.setStreamType(TwitchStream.Type.CANCELLED);
				}

				this.method_7236();
				if (this.streamType == TwitchStream.Type.CANCELLED || this.streamType == TwitchStream.Type.FINISHED) {
					this.method_7237();
				}
			}
		}
	}

	public void shutdown() {
		if (!this.isNotBroadcasting() && !this.isShutdown) {
			this.isShutdown = true;
			if (this.ingestServer != null) {
				this.ingestServer.bitrateKbps = 0.0F;
			}
		}
	}

	protected boolean method_7226(IngestServer ingestServer) {
		this.field_8315 = true;
		this.field_8309 = 0L;
		this.rtmpState = RTMPState.Idle;
		this.ingestServer = ingestServer;
		this.field_11385 = true;
		this.setStreamType(TwitchStream.Type.CONNECTING);
		ErrorCode errorCode = this.stream.start(this.videoArgs, this.audioArgs, ingestServer, StartFlags.TTV_Start_BandwidthTest, true);
		if (ErrorCode.failed(errorCode)) {
			this.field_11385 = false;
			this.field_8315 = false;
			this.setStreamType(TwitchStream.Type.FINISHED_TESTING);
			return false;
		} else {
			this.field_8323 = this.field_8309;
			ingestServer.bitrateKbps = 0.0F;
			this.field_8322 = 0;
			return true;
		}
	}

	protected void method_7227(IngestServer ingestServer) {
		if (this.field_11385) {
			this.field_8320 = true;
		} else if (this.field_11384) {
			this.field_11386 = true;
			ErrorCode errorCode = this.stream.stop(true);
			if (ErrorCode.failed(errorCode)) {
				this.streamCallbacks.stopCallback(ErrorCode.TTV_EC_SUCCESS);
				System.out.println("Stop failed: " + errorCode.toString());
			}

			this.stream.pollStats();
		} else {
			this.streamCallbacks.stopCallback(ErrorCode.TTV_EC_SUCCESS);
		}
	}

	protected long getTestingTime() {
		return System.currentTimeMillis() - this.startTestTime;
	}

	protected void method_7236() {
		float f = (float)this.getTestingTime();
		switch (this.streamType) {
			case STARTING:
			case CONNECTING:
			case UNINITIALIZED:
			case FINISHED:
			case CANCELLED:
			case FAILED:
				this.testCompletePercentage = 0.0F;
				break;
			case FINISHED_TESTING:
				this.testCompletePercentage = 1.0F;
				break;
			case TESTING:
			case CANCELLING:
			default:
				this.testCompletePercentage = f / (float)this.maxTestTime;
		}

		switch (this.streamType) {
			case FINISHED:
			case CANCELLED:
			case FAILED:
				this.field_8324 = 1.0F;
				break;
			default:
				this.field_8324 = (float)this.availableServers / (float)this.ingestList.getServers().length;
				this.field_8324 = this.field_8324 + this.testCompletePercentage / (float)this.ingestList.getServers().length;
		}
	}

	protected boolean method_7229(IngestServer ingestServer) {
		if (this.field_8320 || this.isShutdown || this.getTestingTime() >= this.maxTestTime) {
			this.setStreamType(TwitchStream.Type.FINISHED_TESTING);
			return true;
		} else if (!this.field_11385 && !this.field_11386) {
			ErrorCode errorCode = this.stream.submitVideoFrame((FrameBuffer)this.frameBuffer.get(this.field_8322));
			if (ErrorCode.failed(errorCode)) {
				this.field_8315 = false;
				this.setStreamType(TwitchStream.Type.FINISHED_TESTING);
				return false;
			} else {
				this.field_8322 = (this.field_8322 + 1) % this.frameBuffer.size();
				this.stream.pollStats();
				if (this.rtmpState == RTMPState.SendVideo) {
					this.setStreamType(TwitchStream.Type.TESTING);
					long l = this.getTestingTime();
					if (l > 0L && this.field_8309 > this.field_8323) {
						ingestServer.bitrateKbps = (float)(this.field_8309 * 8L) / (float)this.getTestingTime();
						this.field_8323 = this.field_8309;
					}
				}

				return true;
			}
		} else {
			return true;
		}
	}

	protected void method_7237() {
		this.ingestServer = null;
		if (this.frameBuffer != null) {
			for (int i = 0; i < this.frameBuffer.size(); i++) {
				((FrameBuffer)this.frameBuffer.get(i)).free();
			}

			this.frameBuffer = null;
		}

		if (this.stream.getStatCallbacks() == this.statCallbacks) {
			this.stream.setStatCallbacks(this.field_8317);
			this.field_8317 = null;
		}

		if (this.stream.getStreamCallbacks() == this.streamCallbacks) {
			this.stream.setStreamCallbacks(this.field_8316);
			this.field_8316 = null;
		}
	}

	protected void setStreamType(TwitchStream.Type type) {
		if (type != this.streamType) {
			this.streamType = type;
			if (this.listener != null) {
				this.listener.updateStreamState(this, type);
			}
		}
	}

	public interface Listener {
		void updateStreamState(TwitchStream stream, TwitchStream.Type streamType);
	}

	public static enum Type {
		UNINITIALIZED,
		STARTING,
		CONNECTING,
		TESTING,
		FINISHED_TESTING,
		FINISHED,
		CANCELLING,
		CANCELLED,
		FAILED;
	}
}
