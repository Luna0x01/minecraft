package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GuiUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int propertyId;
	private int value;

	public GuiUpdateS2CPacket() {
	}

	public GuiUpdateS2CPacket(int i, int j, int k) {
		this.id = i;
		this.propertyId = j;
		this.value = k;
	}

	public void method_11447(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGuiUpdate(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readUnsignedByte();
		this.propertyId = packetByteBuf.readShort();
		this.value = packetByteBuf.readShort();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.id);
		packetByteBuf.writeShort(this.propertyId);
		packetByteBuf.writeShort(this.value);
	}

	public int getId() {
		return this.id;
	}

	public int getPropertyId() {
		return this.propertyId;
	}

	public int getValue() {
		return this.value;
	}
}
