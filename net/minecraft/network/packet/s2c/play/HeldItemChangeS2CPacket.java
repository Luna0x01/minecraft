package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class HeldItemChangeS2CPacket implements Packet<ClientPlayPacketListener> {
	private int slot;

	public HeldItemChangeS2CPacket() {
	}

	public HeldItemChangeS2CPacket(int i) {
		this.slot = i;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.slot = buf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.slot);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onHeldItemChange(this);
	}

	public int getSlot() {
		return this.slot;
	}
}
