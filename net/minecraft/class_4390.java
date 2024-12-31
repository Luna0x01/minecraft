package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_4390 implements Packet<ServerPlayPacketListener> {
	private int field_21616;
	private int field_21617;

	public class_4390() {
	}

	public class_4390(int i, int j) {
		this.field_21616 = i;
		this.field_21617 = j;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21616 = buf.readVarInt();
		this.field_21617 = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21616);
		buf.writeVarInt(this.field_21617);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_20282(this);
	}

	public int method_20355() {
		return this.field_21616;
	}

	public int method_20356() {
		return this.field_21617;
	}
}
