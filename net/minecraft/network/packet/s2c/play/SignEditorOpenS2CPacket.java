package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class SignEditorOpenS2CPacket implements Packet<ClientPlayPacketListener> {
	private BlockPos pos;

	public SignEditorOpenS2CPacket() {
	}

	public SignEditorOpenS2CPacket(BlockPos blockPos) {
		this.pos = blockPos;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSignEditorOpen(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
	}

	public BlockPos getPos() {
		return this.pos;
	}
}
