package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityAttachS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int holdingId;

	public EntityAttachS2CPacket() {
	}

	public EntityAttachS2CPacket(Entity entity, @Nullable Entity entity2) {
		this.id = entity.getEntityId();
		this.holdingId = entity2 != null ? entity2.getEntityId() : -1;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readInt();
		this.holdingId = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.id);
		buf.writeInt(this.holdingId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAttach(this);
	}

	public int getId() {
		return this.id;
	}

	public int getHoldingEntityId() {
		return this.holdingId;
	}
}
