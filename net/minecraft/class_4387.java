package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_4387 implements Packet<ServerPlayPacketListener> {
	private int field_21586;

	public class_4387() {
	}

	public class_4387(int i) {
		this.field_21586 = i;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21586 = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21586);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_20279(this);
	}

	public int method_20298() {
		return this.field_21586;
	}
}
