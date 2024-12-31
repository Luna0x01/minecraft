package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class CommandSuggestionsS2CPacket implements Packet<ClientPlayPacketListener> {
	private String[] suggestions;

	public CommandSuggestionsS2CPacket() {
	}

	public CommandSuggestionsS2CPacket(String[] strings) {
		this.suggestions = strings;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.suggestions = new String[buf.readVarInt()];

		for (int i = 0; i < this.suggestions.length; i++) {
			this.suggestions[i] = buf.readString(32767);
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.suggestions.length);

		for (String string : this.suggestions) {
			buf.writeString(string);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCommandSuggestions(this);
	}

	public String[] getSuggestions() {
		return this.suggestions;
	}
}
