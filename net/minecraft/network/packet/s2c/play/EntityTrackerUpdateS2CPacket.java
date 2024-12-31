package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityTrackerUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private List<DataTracker.DataEntry<?>> trackedValues;

	public EntityTrackerUpdateS2CPacket() {
	}

	public EntityTrackerUpdateS2CPacket(int i, DataTracker dataTracker, boolean bl) {
		this.id = i;
		if (bl) {
			this.trackedValues = dataTracker.getEntries();
			dataTracker.clearDirty();
		} else {
			this.trackedValues = dataTracker.getChangedEntries();
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.trackedValues = DataTracker.method_12753(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		DataTracker.method_12749(this.trackedValues, buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityTrackerUpdate(this);
	}

	public List<DataTracker.DataEntry<?>> getTrackedValues() {
		return this.trackedValues;
	}

	public int getId() {
		return this.id;
	}
}
