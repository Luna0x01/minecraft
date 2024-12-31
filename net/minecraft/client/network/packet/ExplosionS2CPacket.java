package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.x = (double)packetByteBuf.readFloat();
		this.y = (double)packetByteBuf.readFloat();
		this.z = (double)packetByteBuf.readFloat();
		this.radius = packetByteBuf.readFloat();
		int i = packetByteBuf.readInt();
		this.affectedBlocks = Lists.newArrayListWithCapacity(i);
		int j = MathHelper.floor(this.x);
		int k = MathHelper.floor(this.y);
		int l = MathHelper.floor(this.z);

		for (int m = 0; m < i; m++) {
			int n = packetByteBuf.readByte() + j;
			int o = packetByteBuf.readByte() + k;
			int p = packetByteBuf.readByte() + l;
			this.affectedBlocks.add(new BlockPos(n, o, p));
		}

		this.playerVelocityX = packetByteBuf.readFloat();
		this.playerVelocityY = packetByteBuf.readFloat();
		this.playerVelocityZ = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeFloat((float)this.x);
		packetByteBuf.writeFloat((float)this.y);
		packetByteBuf.writeFloat((float)this.z);
		packetByteBuf.writeFloat(this.radius);
		packetByteBuf.writeInt(this.affectedBlocks.size());
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y);
		int k = MathHelper.floor(this.z);

		for (BlockPos blockPos : this.affectedBlocks) {
			int l = blockPos.getX() - i;
			int m = blockPos.getY() - j;
			int n = blockPos.getZ() - k;
			packetByteBuf.writeByte(l);
			packetByteBuf.writeByte(m);
			packetByteBuf.writeByte(n);
		}

		packetByteBuf.writeFloat(this.playerVelocityX);
		packetByteBuf.writeFloat(this.playerVelocityY);
		packetByteBuf.writeFloat(this.playerVelocityZ);
	}

	public void method_11480(ClientPlayPacketListener clientPlayPacketListener) {
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
