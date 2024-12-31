package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ChunkRenderDistanceCenterS2CPacket implements Packet<ClientPlayPacketListener> {
	private int chunkX;
	private int chunkZ;

	public ChunkRenderDistanceCenterS2CPacket() {
	}

	public ChunkRenderDistanceCenterS2CPacket(int i, int j) {
		this.chunkX = i;
		this.chunkZ = j;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.chunkX = buf.readVarInt();
		this.chunkZ = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.chunkX);
		buf.writeVarInt(this.chunkZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkRenderDistanceCenter(this);
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkZ() {
		return this.chunkZ;
	}
}
