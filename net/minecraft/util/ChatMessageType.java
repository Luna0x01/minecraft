package net.minecraft.util;

public enum ChatMessageType {
	CHAT((byte)0),
	SYSTEM((byte)1),
	GAME_INFO((byte)2);

	private final byte field_16259;

	private ChatMessageType(byte b) {
		this.field_16259 = b;
	}

	public byte method_14783() {
		return this.field_16259;
	}

	public static ChatMessageType method_14784(byte b) {
		for (ChatMessageType chatMessageType : values()) {
			if (b == chatMessageType.field_16259) {
				return chatMessageType;
			}
		}

		return CHAT;
	}
}
