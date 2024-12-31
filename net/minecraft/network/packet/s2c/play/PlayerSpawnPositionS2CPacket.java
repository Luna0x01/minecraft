package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class PlayerSpawnPositionS2CPacket implements Packet<ClientPlayPacketListener> {
	private BlockPos pos;

	public PlayerSpawnPositionS2CPacket() {
	}

	public PlayerSpawnPositionS2CPacket(BlockPos blockPos) {
		this.pos = blockPos;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerSpawnPosition(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}
}
