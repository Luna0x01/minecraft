package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

public class PlayerSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private UUID uuid;
	private int x;
	private int y;
	private int z;
	private byte yaw;
	private byte pitch;
	private int handStackId;
	private DataTracker tracker;
	private List<DataTracker.DataEntry> dataTrackerEntries;

	public PlayerSpawnS2CPacket() {
	}

	public PlayerSpawnS2CPacket(PlayerEntity playerEntity) {
		this.id = playerEntity.getEntityId();
		this.uuid = playerEntity.getGameProfile().getId();
		this.x = MathHelper.floor(playerEntity.x * 32.0);
		this.y = MathHelper.floor(playerEntity.y * 32.0);
		this.z = MathHelper.floor(playerEntity.z * 32.0);
		this.yaw = (byte)((int)(playerEntity.yaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(playerEntity.pitch * 256.0F / 360.0F));
		ItemStack itemStack = playerEntity.inventory.getMainHandStack();
		this.handStackId = itemStack == null ? 0 : Item.getRawId(itemStack.getItem());
		this.tracker = playerEntity.getDataTracker();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.uuid = buf.readUuid();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.yaw = buf.readByte();
		this.pitch = buf.readByte();
		this.handStackId = buf.readShort();
		this.dataTrackerEntries = DataTracker.deserializePacket(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeUUID(this.uuid);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeByte(this.yaw);
		buf.writeByte(this.pitch);
		buf.writeShort(this.handStackId);
		this.tracker.write(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerSpawn(this);
	}

	public List<DataTracker.DataEntry> getDataTrackerEntries() {
		if (this.dataTrackerEntries == null) {
			this.dataTrackerEntries = this.tracker.getEntries();
		}

		return this.dataTrackerEntries;
	}

	public int getId() {
		return this.id;
	}

	public UUID getPlayerUuid() {
		return this.uuid;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public byte getYaw() {
		return this.yaw;
	}

	public byte getPitch() {
		return this.pitch;
	}

	public int getHandStackId() {
		return this.handStackId;
	}
}
