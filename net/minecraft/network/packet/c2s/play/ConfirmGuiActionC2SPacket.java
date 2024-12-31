package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ConfirmGuiActionC2SPacket implements Packet<ServerPlayPacketListener> {
	private int windowId;
	private short actionId;
	private boolean accepted;

	public ConfirmGuiActionC2SPacket() {
	}

	public ConfirmGuiActionC2SPacket(int i, short s, boolean bl) {
		this.windowId = i;
		this.actionId = s;
		this.accepted = bl;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onConfirmTransaction(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.windowId = buf.readByte();
		this.actionId = buf.readShort();
		this.accepted = buf.readByte() != 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.windowId);
		buf.writeShort(this.actionId);
		buf.writeByte(this.accepted ? 1 : 0);
	}

	public int getWindowId() {
		return this.windowId;
	}

	public short getSyncId() {
		return this.actionId;
	}
}
