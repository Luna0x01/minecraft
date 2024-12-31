package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class SteerBoatC2SPacket implements Packet<ServerPlayPacketListener> {
	private boolean rightPaddleTurning;
	private boolean leftPaddleTurning;

	public SteerBoatC2SPacket() {
	}

	public SteerBoatC2SPacket(boolean bl, boolean bl2) {
		this.rightPaddleTurning = bl;
		this.leftPaddleTurning = bl2;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.rightPaddleTurning = buf.readBoolean();
		this.leftPaddleTurning = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBoolean(this.rightPaddleTurning);
		buf.writeBoolean(this.leftPaddleTurning);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSteerBoat(this);
	}

	public boolean isRightPaddleTurning() {
		return this.rightPaddleTurning;
	}

	public boolean isLeftPaddleTurning() {
		return this.leftPaddleTurning;
	}
}
