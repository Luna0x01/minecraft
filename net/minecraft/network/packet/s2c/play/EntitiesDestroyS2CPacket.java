package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntitiesDestroyS2CPacket implements Packet<ClientPlayPacketListener> {
	private int[] entityIds;

	public EntitiesDestroyS2CPacket() {
	}

	public EntitiesDestroyS2CPacket(int... is) {
		this.entityIds = is;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityIds = new int[buf.readVarInt()];

		for (int i = 0; i < this.entityIds.length; i++) {
			this.entityIds[i] = buf.readVarInt();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityIds.length);

		for (int k : this.entityIds) {
			buf.writeVarInt(k);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitiesDestroy(this);
	}

	public int[] getEntityIds() {
		return this.entityIds;
	}
}
