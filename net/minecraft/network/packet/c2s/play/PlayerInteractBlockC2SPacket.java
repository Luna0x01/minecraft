package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlayerInteractBlockC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private Direction field_13813;
	private Hand field_13814;
	private float distanceX;
	private float distanceY;
	private float distanceZ;

	public PlayerInteractBlockC2SPacket() {
	}

	public PlayerInteractBlockC2SPacket(BlockPos blockPos, Direction direction, Hand hand, float f, float g, float h) {
		this.pos = blockPos;
		this.field_13813 = direction;
		this.field_13814 = hand;
		this.distanceX = f;
		this.distanceY = g;
		this.distanceZ = h;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.field_13813 = buf.readEnumConstant(Direction.class);
		this.field_13814 = buf.readEnumConstant(Hand.class);
		this.distanceX = buf.readFloat();
		this.distanceY = buf.readFloat();
		this.distanceZ = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
		buf.writeEnumConstant(this.field_13813);
		buf.writeEnumConstant(this.field_13814);
		buf.writeFloat(this.distanceX);
		buf.writeFloat(this.distanceY);
		buf.writeFloat(this.distanceZ);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractBlock(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Direction method_12708() {
		return this.field_13813;
	}

	public Hand method_12709() {
		return this.field_13814;
	}

	public float getDistanceX() {
		return this.distanceX;
	}

	public float getDistanceY() {
		return this.distanceY;
	}

	public float getDistanceZ() {
		return this.distanceZ;
	}
}
