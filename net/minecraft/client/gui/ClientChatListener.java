package net.minecraft.client.gui;

import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

public interface ClientChatListener {
	void onChatMessage(MessageType messageType, Text text);
}
