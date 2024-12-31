package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ExplosionS2CPacket implements Packet<ClientPlayPacketListener> {
	private double x;
	private double y;
	private double z;
	private float radius;
	private List<BlockPos> affectedBlocks;
	private float playerVelocityX;
	private float playerVelocityY;
	private float playerVelocityZ;

	public ExplosionS2CPacket() {
	}

	public ExplosionS2CPacket(double d, double e, double f, float g, List<BlockPos> list, Vec3d vec3d) {
		this.x = d;
		this.y = e;
		this.z = f;
		this.radius = g;
		this.affectedBlocks = Lists.newArrayList(list);
		if (vec3d != null) {
			this.playerVelocityX = (float)vec3d.x;
			this.playerVelocityY = (float)vec3d.y;
			this.playerVelocityZ = (float)vec3d.z;
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.x = (double)buf.readFloat();
		this.y = (double)buf.readFloat();
		this.z = (double)buf.readFloat();
		this.radius = buf.readFloat();
		int i = buf.readInt();
		this.affectedBlocks = Lists.newArrayListWithCapacity(i);
		int j = (int)this.x;
		int k = (int)this.y;
		int l = (int)this.z;

		for (int m = 0; m < i; m++) {
			int n = buf.readByte() + j;
			int o = buf.readByte() + k;
			int p = buf.readByte() + l;
			this.affectedBlocks.add(new BlockPos(n, o, p));
		}

		this.playerVelocityX = buf.readFloat();
		this.playerVelocityY = buf.readFloat();
		this.playerVelocityZ = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeFloat((float)this.x);
		buf.writeFloat((float)this.y);
		buf.writeFloat((float)this.z);
		buf.writeFloat(this.radius);
		buf.writeInt(this.affectedBlocks.size());
		int i = (int)this.x;
		int j = (int)this.y;
		int k = (int)this.z;

		for (BlockPos blockPos : this.affectedBlocks) {
			int l = blockPos.getX() - i;
			int m = blockPos.getY() - j;
			int n = blockPos.getZ() - k;
			buf.writeByte(l);
			buf.writeByte(m);
			buf.writeByte(n);
		}

		buf.writeFloat(this.playerVelocityX);
		buf.writeFloat(this.playerVelocityY);
		buf.writeFloat(this.playerVelocityZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onExplosion(this);
	}

	public float getPlayerVelocityX() {
		return this.playerVelocityX;
	}

	public float getPlayerVelocityY() {
		return this.playerVelocityY;
	}

	public float getPlayerVelocityZ() {
		return this.playerVelocityZ;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public float getRadius() {
		return this.radius;
	}

	public List<BlockPos> getAffectedBlocks() {
		return this.affectedBlocks;
	}
}
