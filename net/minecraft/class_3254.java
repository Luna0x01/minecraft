package net.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ChatMessageType;

public class class_3254 implements class_3252 {
	private final MinecraftClient field_15889;

	public class_3254(MinecraftClient minecraftClient) {
		this.field_15889 = minecraftClient;
	}

	@Override
	public void method_14472(ChatMessageType chatMessageType, Text text) {
		this.field_15889.inGameHud.setOverlayMessage(text, false);
	}
}
