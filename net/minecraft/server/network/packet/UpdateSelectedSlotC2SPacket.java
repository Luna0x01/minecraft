package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class UpdateSelectedSlotC2SPacket implements Packet<ServerPlayPacketListener> {
	private int selectedSlot;

	public UpdateSelectedSlotC2SPacket() {
	}

	public UpdateSelectedSlotC2SPacket(int i) {
		this.selectedSlot = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.selectedSlot = packetByteBuf.readShort();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeShort(this.selectedSlot);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateSelectedSlot(this);
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}
}
