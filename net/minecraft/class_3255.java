package net.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ChatMessageType;

public class class_3255 implements class_3252 {
	private final MinecraftClient field_15890;

	public class_3255(MinecraftClient minecraftClient) {
		this.field_15890 = minecraftClient;
	}

	@Override
	public void method_14472(ChatMessageType chatMessageType, Text text) {
		this.field_15890.inGameHud.getChatHud().addMessage(text);
	}
}
