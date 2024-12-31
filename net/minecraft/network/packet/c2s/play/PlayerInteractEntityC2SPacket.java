package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlayerInteractEntityC2SPacket implements Packet<ServerPlayPacketListener> {
	private int entityId;
	private PlayerInteractEntityC2SPacket.Type type;
	private Vec3d hitPosition;

	public PlayerInteractEntityC2SPacket() {
	}

	public PlayerInteractEntityC2SPacket(Entity entity, PlayerInteractEntityC2SPacket.Type type) {
		this.entityId = entity.getEntityId();
		this.type = type;
	}

	public PlayerInteractEntityC2SPacket(Entity entity, Vec3d vec3d) {
		this(entity, PlayerInteractEntityC2SPacket.Type.INTERACT_AT);
		this.hitPosition = vec3d;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.type = buf.readEnumConstant(PlayerInteractEntityC2SPacket.Type.class);
		if (this.type == PlayerInteractEntityC2SPacket.Type.INTERACT_AT) {
			this.hitPosition = new Vec3d((double)buf.readFloat(), (double)buf.readFloat(), (double)buf.readFloat());
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeEnum(this.type);
		if (this.type == PlayerInteractEntityC2SPacket.Type.INTERACT_AT) {
			buf.writeFloat((float)this.hitPosition.x);
			buf.writeFloat((float)this.hitPosition.y);
			buf.writeFloat((float)this.hitPosition.z);
		}
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractEntity(this);
	}

	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}

	public PlayerInteractEntityC2SPacket.Type getType() {
		return this.type;
	}

	public Vec3d getHitPosition() {
		return this.hitPosition;
	}

	public static enum Type {
		INTERACT,
		ATTACK,
		INTERACT_AT;
	}
}
