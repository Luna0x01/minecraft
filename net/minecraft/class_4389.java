package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_4389 implements Packet<ServerPlayPacketListener> {
	private int field_21615;

	public class_4389() {
	}

	public class_4389(int i) {
		this.field_21615 = i;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21615 = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21615);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_20281(this);
	}

	public int method_20353() {
		return this.field_21615;
	}
}
