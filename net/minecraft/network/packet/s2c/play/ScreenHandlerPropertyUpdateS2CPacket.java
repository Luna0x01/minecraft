package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ScreenHandlerPropertyUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private int propertyId;
	private int value;

	public ScreenHandlerPropertyUpdateS2CPacket() {
	}

	public ScreenHandlerPropertyUpdateS2CPacket(int i, int j, int k) {
		this.syncId = i;
		this.propertyId = j;
		this.value = k;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScreenHandlerPropertyUpdate(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readUnsignedByte();
		this.propertyId = buf.readShort();
		this.value = buf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeShort(this.propertyId);
		buf.writeShort(this.value);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getPropertyId() {
		return this.propertyId;
	}

	public int getValue() {
		return this.value;
	}
}
