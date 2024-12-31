package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ChunkUnloadS2CPacket implements Packet<ClientPlayPacketListener> {
	private int chunkX;
	private int chunkZ;

	public ChunkUnloadS2CPacket() {
	}

	public ChunkUnloadS2CPacket(int i, int j) {
		this.chunkX = i;
		this.chunkZ = j;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.chunkX = buf.readInt();
		this.chunkZ = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.chunkX);
		buf.writeInt(this.chunkZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUnloadChunk(this);
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkZ() {
		return this.chunkZ;
	}
}
