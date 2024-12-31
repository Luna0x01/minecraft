package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityVelocityUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int velocityX;
	private int velocityY;
	private int velocityZ;

	public EntityVelocityUpdateS2CPacket() {
	}

	public EntityVelocityUpdateS2CPacket(Entity entity) {
		this(entity.getEntityId(), entity.velocityX, entity.velocityY, entity.velocityZ);
	}

	public EntityVelocityUpdateS2CPacket(int i, double d, double e, double f) {
		this.id = i;
		double g = 3.9;
		if (d < -g) {
			d = -g;
		}

		if (e < -g) {
			e = -g;
		}

		if (f < -g) {
			f = -g;
		}

		if (d > g) {
			d = g;
		}

		if (e > g) {
			e = g;
		}

		if (f > g) {
			f = g;
		}

		this.velocityX = (int)(d * 8000.0);
		this.velocityY = (int)(e * 8000.0);
		this.velocityZ = (int)(f * 8000.0);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.velocityX = buf.readShort();
		this.velocityY = buf.readShort();
		this.velocityZ = buf.readShort();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeShort(this.velocityX);
		buf.writeShort(this.velocityY);
		buf.writeShort(this.velocityZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onVelocityUpdate(this);
	}

	public int getId() {
		return this.id;
	}

	public int getVelocityX() {
		return this.velocityX;
	}

	public int getVelocityY() {
		return this.velocityY;
	}

	public int getVelocityZ() {
		return this.velocityZ;
	}
}
