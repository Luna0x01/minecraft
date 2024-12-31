package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class OpenHorseContainerS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private int slotCount;
	private int horseId;

	public OpenHorseContainerS2CPacket() {
	}

	public OpenHorseContainerS2CPacket(int i, int j, int k) {
		this.syncId = i;
		this.slotCount = j;
		this.horseId = k;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOpenHorseContainer(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readUnsignedByte();
		this.slotCount = packetByteBuf.readVarInt();
		this.horseId = packetByteBuf.readInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.syncId);
		packetByteBuf.writeVarInt(this.slotCount);
		packetByteBuf.writeInt(this.horseId);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getSlotCount() {
		return this.slotCount;
	}

	public int getHorseId() {
		return this.horseId;
	}
}
