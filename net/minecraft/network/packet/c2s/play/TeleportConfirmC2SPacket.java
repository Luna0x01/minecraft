package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class TeleportConfirmC2SPacket implements Packet<ServerPlayPacketListener> {
	private int teleportId;

	public TeleportConfirmC2SPacket() {
	}

	public TeleportConfirmC2SPacket(int i) {
		this.teleportId = i;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.teleportId = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.teleportId);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onTeleportConfirm(this);
	}

	public int getTeleportId() {
		return this.teleportId;
	}
}
