package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private UUID uuid;
	private double field_13740;
	private double field_13741;
	private double field_13742;
	private byte yaw;
	private byte pitch;
	private DataTracker tracker;
	private List<DataTracker.DataEntry<?>> dataTrackerEntries;

	public PlayerSpawnS2CPacket() {
	}

	public PlayerSpawnS2CPacket(PlayerEntity playerEntity) {
		this.id = playerEntity.getEntityId();
		this.uuid = playerEntity.getGameProfile().getId();
		this.field_13740 = playerEntity.x;
		this.field_13741 = playerEntity.y;
		this.field_13742 = playerEntity.z;
		this.yaw = (byte)((int)(playerEntity.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(playerEntity.pitch * 256.0F / 360.0F));
		this.tracker = playerEntity.getDataTracker();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.uuid = buf.readUuid();
		this.field_13740 = buf.readDouble();
		this.field_13741 = buf.readDouble();
		this.field_13742 = buf.readDouble();
		this.yaw = buf.readByte();
		this.pitch = buf.readByte();
		this.dataTrackerEntries = DataTracker.method_12753(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeUuid(this.uuid);
		buf.writeDouble(this.field_13740);
		buf.writeDouble(this.field_13741);
		buf.writeDouble(this.field_13742);
		buf.writeByte(this.yaw);
		buf.writeByte(this.pitch);
		this.tracker.write(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerSpawn(this);
	}

	@Nullable
	public List<DataTracker.DataEntry<?>> getDataTrackerEntries() {
		return this.dataTrackerEntries;
	}

	public int getId() {
		return this.id;
	}

	public UUID getPlayerUuid() {
		return this.uuid;
	}

	public double method_12628() {
		return this.field_13740;
	}

	public double method_12629() {
		return this.field_13741;
	}

	public double method_12630() {
		return this.field_13742;
	}

	public byte getYaw() {
		return this.yaw;
	}

	public byte getPitch() {
		return this.pitch;
	}
}
