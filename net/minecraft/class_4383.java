package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_4383 implements Packet<ClientPlayPacketListener> {
	private class_4488 field_21575;

	public class_4383() {
	}

	public class_4383(class_4488 arg) {
		this.field_21575 = arg;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21575 = class_4488.method_21495(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		this.field_21575.method_21493(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_20204(this);
	}

	public class_4488 method_20275() {
		return this.field_21575;
	}
}
