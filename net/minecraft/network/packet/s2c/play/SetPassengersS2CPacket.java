package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class SetPassengersS2CPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private int[] passengerIds;

	public SetPassengersS2CPacket() {
	}

	public SetPassengersS2CPacket(Entity entity) {
		this.entityId = entity.getEntityId();
		List<Entity> list = entity.getPassengerList();
		this.passengerIds = new int[list.size()];

		for (int i = 0; i < list.size(); i++) {
			this.passengerIds[i] = ((Entity)list.get(i)).getEntityId();
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.passengerIds = buf.readIntArray();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeIntArray(this.passengerIds);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSetPassengers(this);
	}

	public int[] getPassengerIds() {
		return this.passengerIds;
	}

	public int getEntityId() {
		return this.entityId;
	}
}
