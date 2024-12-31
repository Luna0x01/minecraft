package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class RequestCommandCompletionsC2SPacket implements Packet<ServerPlayPacketListener> {
	private int field_21578;
	private String partialCommand;

	public RequestCommandCompletionsC2SPacket() {
	}

	public RequestCommandCompletionsC2SPacket(int i, String string) {
		this.field_21578 = i;
		this.partialCommand = string;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21578 = buf.readVarInt();
		this.partialCommand = buf.readString(32500);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21578);
		buf.method_20167(this.partialCommand, 32500);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onRequestCommandCompletions(this);
	}

	public int method_20289() {
		return this.field_21578;
	}

	public String getPartialCommand() {
		return this.partialCommand;
	}
}
