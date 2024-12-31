package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CustomPayloadC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final Identifier field_21579 = new Identifier("minecraft:brand");
	private Identifier field_21580;
	private PacketByteBuf payload;

	public CustomPayloadC2SPacket() {
	}

	public CustomPayloadC2SPacket(Identifier identifier, PacketByteBuf packetByteBuf) {
		this.field_21580 = identifier;
		this.payload = packetByteBuf;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21580 = buf.readIdentifier();
		int i = buf.readableBytes();
		if (i >= 0 && i <= 32767) {
			this.payload = new PacketByteBuf(buf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 32767 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeIdentifier(this.field_21580);
		buf.writeBytes(this.payload);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCustomPayload(this);
		if (this.payload != null) {
			this.payload.release();
		}
	}
}
