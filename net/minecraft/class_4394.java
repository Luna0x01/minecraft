package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class class_4394 implements Packet<ClientLoginPacketListener> {
	private int field_21641;
	private Identifier field_21642;
	private PacketByteBuf field_21643;

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21641 = buf.readVarInt();
		this.field_21642 = buf.readIdentifier();
		int i = buf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.field_21643 = new PacketByteBuf(buf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21641);
		buf.writeIdentifier(this.field_21642);
		buf.writeBytes(this.field_21643.copy());
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.method_20383(this);
	}

	public int method_20385() {
		return this.field_21641;
	}
}
