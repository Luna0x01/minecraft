package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CustomPayloadS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final Identifier field_21531 = new Identifier("minecraft:trader_list");
	public static final Identifier field_21532 = new Identifier("minecraft:brand");
	public static final Identifier field_21533 = new Identifier("minecraft:book_open");
	public static final Identifier field_21534 = new Identifier("minecraft:debug/path");
	public static final Identifier field_21535 = new Identifier("minecraft:debug/neighbors_update");
	public static final Identifier field_21536 = new Identifier("minecraft:debug/caves");
	public static final Identifier field_21537 = new Identifier("minecraft:debug/structures");
	public static final Identifier field_21538 = new Identifier("minecraft:debug/worldgen_attempt");
	private Identifier field_21539;
	private PacketByteBuf payload;

	public CustomPayloadS2CPacket() {
	}

	public CustomPayloadS2CPacket(Identifier identifier, PacketByteBuf packetByteBuf) {
		this.field_21539 = identifier;
		this.payload = packetByteBuf;
		if (packetByteBuf.writerIndex() > 1048576) {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21539 = buf.readIdentifier();
		int i = buf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.payload = new PacketByteBuf(buf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeIdentifier(this.field_21539);
		buf.writeBytes(this.payload.copy());
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCustomPayload(this);
	}

	public Identifier method_7733() {
		return this.field_21539;
	}

	public PacketByteBuf getPayload() {
		return new PacketByteBuf(this.payload.copy());
	}
}
