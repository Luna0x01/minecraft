package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityAttachS2CPacket implements Packet<ClientPlayPacketListener> {
	private int attachedId;
	private int holdingId;

	public EntityAttachS2CPacket() {
	}

	public EntityAttachS2CPacket(Entity entity, @Nullable Entity entity2) {
		this.attachedId = entity.getEntityId();
		this.holdingId = entity2 != null ? entity2.getEntityId() : 0;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.attachedId = packetByteBuf.readInt();
		this.holdingId = packetByteBuf.readInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.attachedId);
		packetByteBuf.writeInt(this.holdingId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAttach(this);
	}

	public int getAttachedEntityId() {
		return this.attachedId;
	}

	public int getHoldingEntityId() {
		return this.holdingId;
	}
}
