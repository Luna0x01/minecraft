package net.minecraft.client.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

public class ChatListenerHud implements ClientChatListener {
	private final MinecraftClient client;

	public ChatListenerHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void onChatMessage(MessageType messageType, Text text) {
		this.client.inGameHud.getChatHud().addMessage(text);
	}
}
