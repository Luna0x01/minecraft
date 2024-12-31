package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ConfirmScreenActionS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private short actionId;
	private boolean accepted;

	public ConfirmScreenActionS2CPacket() {
	}

	public ConfirmScreenActionS2CPacket(int syncId, short actionId, boolean accepted) {
		this.syncId = syncId;
		this.actionId = actionId;
		this.accepted = accepted;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onConfirmScreenAction(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readUnsignedByte();
		this.actionId = buf.readShort();
		this.accepted = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeShort(this.actionId);
		buf.writeBoolean(this.accepted);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public short getActionId() {
		return this.actionId;
	}

	public boolean wasAccepted() {
		return this.accepted;
	}
}
