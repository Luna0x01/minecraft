package net.minecraft.client.gui;

import java.util.UUID;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

public interface ClientChatListener {
	void onChatMessage(MessageType messageType, Text message, UUID sender);
}
