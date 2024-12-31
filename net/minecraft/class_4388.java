package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_4388 implements Packet<ServerPlayPacketListener> {
	private String field_21591;

	public class_4388() {
	}

	public class_4388(String string) {
		this.field_21591 = string;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21591 = buf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.field_21591);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_20280(this);
	}

	public String method_20303() {
		return this.field_21591;
	}
}
