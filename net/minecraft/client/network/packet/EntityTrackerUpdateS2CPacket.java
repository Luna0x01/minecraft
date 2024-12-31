package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityTrackerUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private List<DataTracker.Entry<?>> trackedValues;

	public EntityTrackerUpdateS2CPacket() {
	}

	public EntityTrackerUpdateS2CPacket(int i, DataTracker dataTracker, boolean bl) {
		this.id = i;
		if (bl) {
			this.trackedValues = dataTracker.getAllEntries();
			dataTracker.clearDirty();
		} else {
			this.trackedValues = dataTracker.getDirtyEntries();
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.trackedValues = DataTracker.deserializePacket(packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		DataTracker.entriesToPacket(this.trackedValues, packetByteBuf);
	}

	public void method_11808(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityTrackerUpdate(this);
	}

	public List<DataTracker.Entry<?>> getTrackedValues() {
		return this.trackedValues;
	}

	public int id() {
		return this.id;
	}
}
