package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.chunk.Chunk;

public class ChunkMapS2CPacket implements Packet<ClientPlayPacketListener> {
	private int[] xPositions;
	private int[] zPositions;
	private ChunkDataS2CPacket.ExtraData[] data;
	private boolean notNether;

	public ChunkMapS2CPacket() {
	}

	public ChunkMapS2CPacket(List<Chunk> list) {
		int i = list.size();
		this.xPositions = new int[i];
		this.zPositions = new int[i];
		this.data = new ChunkDataS2CPacket.ExtraData[i];
		this.notNether = !((Chunk)list.get(0)).getWorld().dimension.hasNoSkylight();

		for (int j = 0; j < i; j++) {
			Chunk chunk = (Chunk)list.get(j);
			ChunkDataS2CPacket.ExtraData extraData = ChunkDataS2CPacket.createExtraData(chunk, true, this.notNether, 65535);
			this.xPositions[j] = chunk.chunkX;
			this.zPositions[j] = chunk.chunkZ;
			this.data[j] = extraData;
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.notNether = buf.readBoolean();
		int i = buf.readVarInt();
		this.xPositions = new int[i];
		this.zPositions = new int[i];
		this.data = new ChunkDataS2CPacket.ExtraData[i];

		for (int j = 0; j < i; j++) {
			this.xPositions[j] = buf.readInt();
			this.zPositions[j] = buf.readInt();
			this.data[j] = new ChunkDataS2CPacket.ExtraData();
			this.data[j].size = buf.readShort() & '\uffff';
			this.data[j].bytes = new byte[ChunkDataS2CPacket.method_10659(Integer.bitCount(this.data[j].size), this.notNether, true)];
		}

		for (int k = 0; k < i; k++) {
			buf.readBytes(this.data[k].bytes);
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBoolean(this.notNether);
		buf.writeVarInt(this.data.length);

		for (int i = 0; i < this.xPositions.length; i++) {
			buf.writeInt(this.xPositions[i]);
			buf.writeInt(this.zPositions[i]);
			buf.writeShort((short)(this.data[i].size & 65535));
		}

		for (int j = 0; j < this.xPositions.length; j++) {
			buf.writeBytes(this.data[j].bytes);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkMap(this);
	}

	public int getXPos(int index) {
		return this.xPositions[index];
	}

	public int getZPos(int index) {
		return this.zPositions[index];
	}

	public int getXPosLength() {
		return this.xPositions.length;
	}

	public byte[] getDataBytes(int index) {
		return this.data[index].bytes;
	}

	public int etDataSize(int index) {
		return this.data[index].size;
	}
}
