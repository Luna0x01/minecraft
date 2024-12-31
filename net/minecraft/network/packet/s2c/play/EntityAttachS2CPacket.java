package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityAttachS2CPacket implements Packet<ClientPlayPacketListener> {
	private int attachedId;
	private int id;
	private int holdingId;

	public EntityAttachS2CPacket() {
	}

	public EntityAttachS2CPacket(int i, Entity entity, Entity entity2) {
		this.attachedId = i;
		this.id = entity.getEntityId();
		this.holdingId = entity2 != null ? entity2.getEntityId() : -1;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readInt();
		this.holdingId = buf.readInt();
		this.attachedId = buf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.id);
		buf.writeInt(this.holdingId);
		buf.writeByte(this.attachedId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAttach(this);
	}

	public int getAttachedEntityId() {
		return this.attachedId;
	}

	public int getId() {
		return this.id;
	}

	public int getHoldingEntityId() {
		return this.holdingId;
	}
}
