package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_4386 implements Packet<ServerPlayPacketListener> {
	private int field_21584;
	private int field_21585;

	public class_4386() {
	}

	public class_4386(int i, int j) {
		this.field_21584 = i;
		this.field_21585 = j;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21584 = buf.readVarInt();
		this.field_21585 = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21584);
		buf.writeVarInt(this.field_21585);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_20278(this);
	}

	public int method_20295() {
		return this.field_21584;
	}

	public int method_20296() {
		return this.field_21585;
	}
}
