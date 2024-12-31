package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ConfirmGuiActionS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private short actionId;
	private boolean accepted;

	public ConfirmGuiActionS2CPacket() {
	}

	public ConfirmGuiActionS2CPacket(int i, short s, boolean bl) {
		this.id = i;
		this.actionId = s;
		this.accepted = bl;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGuiActionConfirm(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readUnsignedByte();
		this.actionId = buf.readShort();
		this.accepted = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.id);
		buf.writeShort(this.actionId);
		buf.writeBoolean(this.accepted);
	}

	public int getId() {
		return this.id;
	}

	public short getActionId() {
		return this.actionId;
	}

	public boolean wasAccepted() {
		return this.accepted;
	}
}
