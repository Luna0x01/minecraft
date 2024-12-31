package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ChunkLoadDistanceS2CPacket implements Packet<ClientPlayPacketListener> {
	private Item item;
	private int distance;

	public ChunkLoadDistanceS2CPacket() {
	}

	public ChunkLoadDistanceS2CPacket(Item item, int i) {
		this.item = item;
		this.distance = i;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.item = Item.byRawId(buf.readVarInt());
		this.distance = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(Item.getRawId(this.item));
		buf.writeVarInt(this.distance);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkLoadDistance(this);
	}

	public Item getItem() {
		return this.item;
	}

	public int getDistance() {
		return this.distance;
	}
}
