package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerInputC2SPacket implements Packet<ServerPlayPacketListener> {
	private float sideways;
	private float forward;
	private boolean jumping;
	private boolean sneaking;

	public PlayerInputC2SPacket() {
	}

	public PlayerInputC2SPacket(float f, float g, boolean bl, boolean bl2) {
		this.sideways = f;
		this.forward = g;
		this.jumping = bl;
		this.sneaking = bl2;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.sideways = buf.readFloat();
		this.forward = buf.readFloat();
		byte b = buf.readByte();
		this.jumping = (b & 1) > 0;
		this.sneaking = (b & 2) > 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeFloat(this.sideways);
		buf.writeFloat(this.forward);
		byte b = 0;
		if (this.jumping) {
			b = (byte)(b | 1);
		}

		if (this.sneaking) {
			b = (byte)(b | 2);
		}

		buf.writeByte(b);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInput(this);
	}

	public float getSideways() {
		return this.sideways;
	}

	public float getForward() {
		return this.forward;
	}

	public boolean isJumping() {
		return this.jumping;
	}

	public boolean isSneaking() {
		return this.sneaking;
	}
}
