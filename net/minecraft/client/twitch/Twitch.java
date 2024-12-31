package net.minecraft.client.twitch;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.twitch.AuthToken;
import tv.twitch.Core;
import tv.twitch.ErrorCode;
import tv.twitch.StandardCoreAPI;
import tv.twitch.chat.Chat;
import tv.twitch.chat.ChatBadgeData;
import tv.twitch.chat.ChatChannelInfo;
import tv.twitch.chat.ChatEmoticonData;
import tv.twitch.chat.ChatEvent;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizationOption;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.IChatAPIListener;
import tv.twitch.chat.IChatChannelListener;
import tv.twitch.chat.StandardChatAPI;

public class Twitch {
	private static final Logger LOGGER = LogManager.getLogger();
	protected Twitch.Listener listener = null;
	protected String username = "";
	protected String clientId = "";
	protected String field_8282 = "";
	protected Core core = null;
	protected Chat chat = null;
	protected Twitch.StreamState state = Twitch.StreamState.UNINITIALIZED;
	protected AuthToken authToken = new AuthToken();
	protected HashMap<String, Twitch.ChatListener> chatListeners = new HashMap();
	protected int field_8290 = 128;
	protected Twitch.TextureType field_11352 = Twitch.TextureType.NONE;
	protected Twitch.TextureType field_11353 = Twitch.TextureType.NONE;
	protected ChatEmoticonData emoticonData = null;
	protected int messageFlushInterval = 500;
	protected int userChangeEventInterval = 2000;
	protected IChatAPIListener chatListener = new IChatAPIListener() {
		public void chatInitializationCallback(ErrorCode code) {
			if (ErrorCode.succeeded(code)) {
				Twitch.this.chat.setMessageFlushInterval(Twitch.this.messageFlushInterval);
				Twitch.this.chat.setUserChangeEventInterval(Twitch.this.userChangeEventInterval);
				Twitch.this.method_7216();
				Twitch.this.setState(Twitch.StreamState.INITIALIZED);
			} else {
				Twitch.this.setState(Twitch.StreamState.UNINITIALIZED);
			}

			try {
				if (Twitch.this.listener != null) {
					Twitch.this.listener.logChatStart(code);
				}
			} catch (Exception var3) {
				Twitch.this.logError(var3.toString());
			}
		}

		public void chatShutdownCallback(ErrorCode code) {
			if (ErrorCode.succeeded(code)) {
				ErrorCode errorCode = Twitch.this.core.shutdown();
				if (ErrorCode.failed(errorCode)) {
					String string = ErrorCode.getString(errorCode);
					Twitch.this.logError(String.format("Error shutting down the Twitch sdk: %s", string));
				}

				Twitch.this.setState(Twitch.StreamState.UNINITIALIZED);
			} else {
				Twitch.this.setState(Twitch.StreamState.INITIALIZED);
				Twitch.this.logError(String.format("Error shutting down Twith chat: %s", code));
			}

			try {
				if (Twitch.this.listener != null) {
					Twitch.this.listener.logChatStop(code);
				}
			} catch (Exception var4) {
				Twitch.this.logError(var4.toString());
			}
		}

		public void chatEmoticonDataDownloadCallback(ErrorCode code) {
			if (ErrorCode.succeeded(code)) {
				Twitch.this.method_7217();
			}
		}
	};

	public void setListener(Twitch.Listener listener) {
		this.listener = listener;
	}

	public void setAuthToken(AuthToken authToken) {
		this.authToken = authToken;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Twitch.StreamState getStreamState() {
		return this.state;
	}

	public boolean isChatListenerConnected(String chatId) {
		if (!this.chatListeners.containsKey(chatId)) {
			return false;
		} else {
			Twitch.ChatListener chatListener = (Twitch.ChatListener)this.chatListeners.get(chatId);
			return chatListener.getNetworkState() == Twitch.NetworkState.CONNECTED;
		}
	}

	public Twitch.NetworkState getChatNetworkState(String chatId) {
		if (!this.chatListeners.containsKey(chatId)) {
			return Twitch.NetworkState.DISCONNECTED;
		} else {
			Twitch.ChatListener chatListener = (Twitch.ChatListener)this.chatListeners.get(chatId);
			return chatListener.getNetworkState();
		}
	}

	public Twitch() {
		this.core = Core.getInstance();
		if (this.core == null) {
			this.core = new Core(new StandardCoreAPI());
		}

		this.chat = new Chat(new StandardChatAPI());
	}

	public boolean method_10444() {
		if (this.state != Twitch.StreamState.UNINITIALIZED) {
			return false;
		} else {
			this.setState(Twitch.StreamState.INITIALIZING);
			ErrorCode errorCode = this.core.initialize(this.clientId, null);
			if (ErrorCode.failed(errorCode)) {
				this.setState(Twitch.StreamState.UNINITIALIZED);
				String string = ErrorCode.getString(errorCode);
				this.logError(String.format("Error initializing Twitch sdk: %s", string));
				return false;
			} else {
				this.field_11353 = this.field_11352;
				HashSet<ChatTokenizationOption> hashSet = new HashSet();
				switch (this.field_11352) {
					case NONE:
						hashSet.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_NONE);
						break;
					case URL:
						hashSet.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS);
						break;
					case TEXTURE_ATLAS:
						hashSet.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES);
				}

				errorCode = this.chat.initialize(hashSet, this.chatListener);
				if (ErrorCode.failed(errorCode)) {
					this.core.shutdown();
					this.setState(Twitch.StreamState.UNINITIALIZED);
					String string2 = ErrorCode.getString(errorCode);
					this.logError(String.format("Error initializing Twitch chat: %s", string2));
					return false;
				} else {
					this.setState(Twitch.StreamState.INITIALIZED);
					return true;
				}
			}
		}
	}

	public boolean method_7205(String chatId) {
		return this.method_10440(chatId, false);
	}

	protected boolean method_10440(String chatId, boolean bl) {
		if (this.state != Twitch.StreamState.INITIALIZED) {
			return false;
		} else if (this.chatListeners.containsKey(chatId)) {
			this.logError("Already in channel: " + chatId);
			return false;
		} else if (chatId != null && !chatId.equals("")) {
			Twitch.ChatListener chatListener = new Twitch.ChatListener(chatId);
			this.chatListeners.put(chatId, chatListener);
			boolean bl2 = chatListener.method_10449(bl);
			if (!bl2) {
				this.chatListeners.remove(chatId);
			}

			return bl2;
		} else {
			return false;
		}
	}

	public boolean method_10443(String chatId) {
		if (this.state != Twitch.StreamState.INITIALIZED) {
			return false;
		} else if (!this.chatListeners.containsKey(chatId)) {
			this.logError("Not in channel: " + chatId);
			return false;
		} else {
			Twitch.ChatListener chatListener = (Twitch.ChatListener)this.chatListeners.get(chatId);
			return chatListener.method_10453();
		}
	}

	public boolean stopChat() {
		if (this.state != Twitch.StreamState.INITIALIZED) {
			return false;
		} else {
			ErrorCode errorCode = this.chat.shutdown();
			if (ErrorCode.failed(errorCode)) {
				String string = ErrorCode.getString(errorCode);
				this.logError(String.format("Error shutting down chat: %s", string));
				return false;
			} else {
				this.clearEmoticonData();
				this.setState(Twitch.StreamState.SHUTTING_DOWN);
				return true;
			}
		}
	}

	public void stop() {
		if (this.getStreamState() != Twitch.StreamState.UNINITIALIZED) {
			this.stopChat();
			if (this.getStreamState() == Twitch.StreamState.SHUTTING_DOWN) {
				while (this.getStreamState() != Twitch.StreamState.UNINITIALIZED) {
					try {
						Thread.sleep(200L);
						this.flushChatEvents();
					} catch (InterruptedException var2) {
					}
				}
			}
		}
	}

	public void flushChatEvents() {
		if (this.state != Twitch.StreamState.UNINITIALIZED) {
			ErrorCode errorCode = this.chat.flushEvents();
			if (ErrorCode.failed(errorCode)) {
				String string = ErrorCode.getString(errorCode);
				this.logError(String.format("Error flushing chat events: %s", string));
			}
		}
	}

	public boolean sendChatMessage(String chatId, String chatMessage) {
		if (this.state != Twitch.StreamState.INITIALIZED) {
			return false;
		} else if (!this.chatListeners.containsKey(chatId)) {
			this.logError("Not in channel: " + chatId);
			return false;
		} else {
			Twitch.ChatListener chatListener = (Twitch.ChatListener)this.chatListeners.get(chatId);
			return chatListener.sendChatMessage(chatMessage);
		}
	}

	protected void setState(Twitch.StreamState state) {
		if (state != this.state) {
			this.state = state;

			try {
				if (this.listener != null) {
					this.listener.setState(state);
				}
			} catch (Exception var3) {
				this.logError(var3.toString());
			}
		}
	}

	protected void method_7216() {
		if (this.field_11353 != Twitch.TextureType.NONE) {
			if (this.emoticonData == null) {
				ErrorCode errorCode = this.chat.downloadEmoticonData();
				if (ErrorCode.failed(errorCode)) {
					String string = ErrorCode.getString(errorCode);
					this.logError(String.format("Error trying to download emoticon data: %s", string));
				}
			}
		}
	}

	protected void method_7217() {
		if (this.emoticonData == null) {
			this.emoticonData = new ChatEmoticonData();
			ErrorCode errorCode = this.chat.getEmoticonData(this.emoticonData);
			if (ErrorCode.succeeded(errorCode)) {
				try {
					if (this.listener != null) {
						this.listener.method_10465();
					}
				} catch (Exception var3) {
					this.logError(var3.toString());
				}
			} else {
				this.logError("Error preparing emoticon data: " + ErrorCode.getString(errorCode));
			}
		}
	}

	protected void clearEmoticonData() {
		if (this.emoticonData != null) {
			ErrorCode errorCode = this.chat.clearEmoticonData();
			if (ErrorCode.succeeded(errorCode)) {
				this.emoticonData = null;

				try {
					if (this.listener != null) {
						this.listener.method_10468();
					}
				} catch (Exception var3) {
					this.logError(var3.toString());
				}
			} else {
				this.logError("Error clearing emoticon data: " + ErrorCode.getString(errorCode));
			}
		}
	}

	protected void logError(String message) {
		LOGGER.error(TwitchAuth.streamMarker, "[Chat controller] {}", new Object[]{message});
	}

	public class ChatListener implements IChatChannelListener {
		protected String field_11368 = null;
		protected boolean field_11369 = false;
		protected Twitch.NetworkState networkState = Twitch.NetworkState.CREATED;
		protected List<ChatUserInfo> chatUserInfos = Lists.newArrayList();
		protected LinkedList<ChatRawMessage> chatRawMessages = new LinkedList();
		protected LinkedList<ChatTokenizedMessage> chatTokenizedMessages = new LinkedList();
		protected ChatBadgeData chatBadgeData = null;

		public ChatListener(String string) {
			this.field_11368 = string;
		}

		public Twitch.NetworkState getNetworkState() {
			return this.networkState;
		}

		public boolean method_10449(boolean bl) {
			this.field_11369 = bl;
			ErrorCode errorCode = ErrorCode.TTV_EC_SUCCESS;
			if (bl) {
				errorCode = Twitch.this.chat.connectAnonymous(this.field_11368, this);
			} else {
				errorCode = Twitch.this.chat.connect(this.field_11368, Twitch.this.username, Twitch.this.authToken.data, this);
			}

			if (ErrorCode.failed(errorCode)) {
				String string = ErrorCode.getString(errorCode);
				Twitch.this.logError(String.format("Error connecting: %s", string));
				this.method_10452(this.field_11368);
				return false;
			} else {
				this.setNetworkState(Twitch.NetworkState.CONNECTING);
				this.method_10454();
				return true;
			}
		}

		public boolean method_10453() {
			switch (this.networkState) {
				case CONNECTED:
				case CONNECTING:
					ErrorCode errorCode = Twitch.this.chat.disconnect(this.field_11368);
					if (ErrorCode.failed(errorCode)) {
						String string = ErrorCode.getString(errorCode);
						Twitch.this.logError(String.format("Error disconnecting: %s", string));
						return false;
					}

					this.setNetworkState(Twitch.NetworkState.DISCONNECTING);
					return true;
				case CREATED:
				case DISCONNECTED:
				case DISCONNECTING:
				default:
					return false;
			}
		}

		protected void setNetworkState(Twitch.NetworkState networkState) {
			if (networkState != this.networkState) {
				this.networkState = networkState;
			}
		}

		public void method_10448(String username) {
			if (Twitch.this.field_11353 == Twitch.TextureType.NONE) {
				this.chatRawMessages.clear();
				this.chatTokenizedMessages.clear();
			} else {
				if (this.chatRawMessages.size() > 0) {
					ListIterator<ChatRawMessage> listIterator = this.chatRawMessages.listIterator();

					while (listIterator.hasNext()) {
						ChatRawMessage chatRawMessage = (ChatRawMessage)listIterator.next();
						if (chatRawMessage.userName.equals(username)) {
							listIterator.remove();
						}
					}
				}

				if (this.chatTokenizedMessages.size() > 0) {
					ListIterator<ChatTokenizedMessage> listIterator2 = this.chatTokenizedMessages.listIterator();

					while (listIterator2.hasNext()) {
						ChatTokenizedMessage chatTokenizedMessage = (ChatTokenizedMessage)listIterator2.next();
						if (chatTokenizedMessage.displayName.equals(username)) {
							listIterator2.remove();
						}
					}
				}
			}

			try {
				if (Twitch.this.listener != null) {
					Twitch.this.listener.method_10460(this.field_11368, username);
				}
			} catch (Exception var4) {
				Twitch.this.logError(var4.toString());
			}
		}

		public boolean sendChatMessage(String message) {
			if (this.networkState != Twitch.NetworkState.CONNECTED) {
				return false;
			} else {
				ErrorCode errorCode = Twitch.this.chat.sendMessage(this.field_11368, message);
				if (ErrorCode.failed(errorCode)) {
					String string = ErrorCode.getString(errorCode);
					Twitch.this.logError(String.format("Error sending chat message: %s", string));
					return false;
				} else {
					return true;
				}
			}
		}

		protected void method_10454() {
			if (Twitch.this.field_11353 != Twitch.TextureType.NONE) {
				if (this.chatBadgeData == null) {
					ErrorCode errorCode = Twitch.this.chat.downloadBadgeData(this.field_11368);
					if (ErrorCode.failed(errorCode)) {
						String string = ErrorCode.getString(errorCode);
						Twitch.this.logError(String.format("Error trying to download badge data: %s", string));
					}
				}
			}
		}

		protected void method_10455() {
			if (this.chatBadgeData == null) {
				this.chatBadgeData = new ChatBadgeData();
				ErrorCode errorCode = Twitch.this.chat.getBadgeData(this.field_11368, this.chatBadgeData);
				if (ErrorCode.succeeded(errorCode)) {
					try {
						if (Twitch.this.listener != null) {
							Twitch.this.listener.method_10464(this.field_11368);
						}
					} catch (Exception var3) {
						Twitch.this.logError(var3.toString());
					}
				} else {
					Twitch.this.logError("Error preparing badge data: " + ErrorCode.getString(errorCode));
				}
			}
		}

		protected void method_10456() {
			if (this.chatBadgeData != null) {
				ErrorCode errorCode = Twitch.this.chat.clearBadgeData(this.field_11368);
				if (ErrorCode.succeeded(errorCode)) {
					this.chatBadgeData = null;

					try {
						if (Twitch.this.listener != null) {
							Twitch.this.listener.method_10466(this.field_11368);
						}
					} catch (Exception var3) {
						Twitch.this.logError(var3.toString());
					}
				} else {
					Twitch.this.logError("Error releasing badge data: " + ErrorCode.getString(errorCode));
				}
			}
		}

		protected void method_10451(String string) {
			try {
				if (Twitch.this.listener != null) {
					Twitch.this.listener.connectChat(string);
				}
			} catch (Exception var3) {
				Twitch.this.logError(var3.toString());
			}
		}

		protected void method_10452(String string) {
			try {
				if (Twitch.this.listener != null) {
					Twitch.this.listener.disconnectChat(string);
				}
			} catch (Exception var3) {
				Twitch.this.logError(var3.toString());
			}
		}

		private void disconnect() {
			if (this.networkState != Twitch.NetworkState.DISCONNECTED) {
				this.setNetworkState(Twitch.NetworkState.DISCONNECTED);
				this.method_10452(this.field_11368);
				this.method_10456();
			}
		}

		public void chatStatusCallback(String string, ErrorCode code) {
			if (!ErrorCode.succeeded(code)) {
				Twitch.this.chatListeners.remove(string);
				this.disconnect();
			}
		}

		public void chatChannelMembershipCallback(String string, ChatEvent event, ChatChannelInfo channelInfo) {
			switch (event) {
				case TTV_CHAT_JOINED_CHANNEL:
					this.setNetworkState(Twitch.NetworkState.CONNECTED);
					this.method_10451(string);
					break;
				case TTV_CHAT_LEFT_CHANNEL:
					this.disconnect();
			}
		}

		public void chatChannelUserChangeCallback(String string, ChatUserInfo[] usersToAdd, ChatUserInfo[] usersToRemove, ChatUserInfo[] usersToReset) {
			for (int i = 0; i < usersToRemove.length; i++) {
				int j = this.chatUserInfos.indexOf(usersToRemove[i]);
				if (j >= 0) {
					this.chatUserInfos.remove(j);
				}
			}

			for (int k = 0; k < usersToReset.length; k++) {
				int l = this.chatUserInfos.indexOf(usersToReset[k]);
				if (l >= 0) {
					this.chatUserInfos.remove(l);
				}

				this.chatUserInfos.add(usersToReset[k]);
			}

			for (int m = 0; m < usersToAdd.length; m++) {
				this.chatUserInfos.add(usersToAdd[m]);
			}

			try {
				if (Twitch.this.listener != null) {
					Twitch.this.listener.method_7220(this.field_11368, usersToAdd, usersToRemove, usersToReset);
				}
			} catch (Exception var7) {
				Twitch.this.logError(var7.toString());
			}
		}

		public void chatChannelRawMessageCallback(String string, ChatRawMessage[] messagesToAdd) {
			for (int i = 0; i < messagesToAdd.length; i++) {
				this.chatRawMessages.addLast(messagesToAdd[i]);
			}

			try {
				if (Twitch.this.listener != null) {
					Twitch.this.listener.method_10461(this.field_11368, messagesToAdd);
				}
			} catch (Exception var4) {
				Twitch.this.logError(var4.toString());
			}

			while (this.chatRawMessages.size() > Twitch.this.field_8290) {
				this.chatRawMessages.removeFirst();
			}
		}

		public void chatChannelTokenizedMessageCallback(String string, ChatTokenizedMessage[] chatTokenizedMessages) {
			for (int i = 0; i < chatTokenizedMessages.length; i++) {
				this.chatTokenizedMessages.addLast(chatTokenizedMessages[i]);
			}

			try {
				if (Twitch.this.listener != null) {
					Twitch.this.listener.method_10462(this.field_11368, chatTokenizedMessages);
				}
			} catch (Exception var4) {
				Twitch.this.logError(var4.toString());
			}

			while (this.chatTokenizedMessages.size() > Twitch.this.field_8290) {
				this.chatTokenizedMessages.removeFirst();
			}
		}

		public void chatClearCallback(String string, String string2) {
			this.method_10448(string2);
		}

		public void chatBadgeDataDownloadCallback(String string, ErrorCode errorCode) {
			if (ErrorCode.succeeded(errorCode)) {
				this.method_10455();
			}
		}
	}

	public interface Listener {
		void logChatStart(ErrorCode code);

		void logChatStop(ErrorCode code);

		void method_10465();

		void method_10468();

		void setState(Twitch.StreamState state);

		void method_10462(String string, ChatTokenizedMessage[] messages);

		void method_10461(String string, ChatRawMessage[] messages);

		void method_7220(String string, ChatUserInfo[] chatUserInfos, ChatUserInfo[] chatUserInfos2, ChatUserInfo[] chatUserInfos3);

		void connectChat(String id);

		void disconnectChat(String id);

		void method_10460(String string, String string2);

		void method_10464(String string);

		void method_10466(String string);
	}

	public static enum NetworkState {
		CREATED,
		CONNECTING,
		CONNECTED,
		DISCONNECTING,
		DISCONNECTED;
	}

	public static enum StreamState {
		UNINITIALIZED,
		INITIALIZING,
		INITIALIZED,
		SHUTTING_DOWN;
	}

	public static enum TextureType {
		NONE,
		URL,
		TEXTURE_ATLAS;
	}
}
