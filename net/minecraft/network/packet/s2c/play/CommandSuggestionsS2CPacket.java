package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.PacketByteBuf;

public class CommandSuggestionsS2CPacket implements Packet<ClientPlayPacketListener> {
	private int field_21523;
	private Suggestions field_21524;

	public CommandSuggestionsS2CPacket() {
	}

	public CommandSuggestionsS2CPacket(int i, Suggestions suggestions) {
		this.field_21523 = i;
		this.field_21524 = suggestions;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21523 = buf.readVarInt();
		int i = buf.readVarInt();
		int j = buf.readVarInt();
		StringRange stringRange = StringRange.between(i, i + j);
		int k = buf.readVarInt();
		List<Suggestion> list = Lists.newArrayListWithCapacity(k);

		for (int l = 0; l < k; l++) {
			String string = buf.readString(32767);
			Text text = buf.readBoolean() ? buf.readText() : null;
			list.add(new Suggestion(stringRange, string, text));
		}

		this.field_21524 = new Suggestions(stringRange, list);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21523);
		buf.writeVarInt(this.field_21524.getRange().getStart());
		buf.writeVarInt(this.field_21524.getRange().getLength());
		buf.writeVarInt(this.field_21524.getList().size());

		for (Suggestion suggestion : this.field_21524.getList()) {
			buf.writeString(suggestion.getText());
			buf.writeBoolean(suggestion.getTooltip() != null);
			if (suggestion.getTooltip() != null) {
				buf.writeText(ChatSerializer.method_20187(suggestion.getTooltip()));
			}
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCommandSuggestions(this);
	}

	public int method_20208() {
		return this.field_21523;
	}

	public Suggestions method_20209() {
		return this.field_21524;
	}
}
