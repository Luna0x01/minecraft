package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

public class ChunkDeltaUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private ChunkPos pos;
	private ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[] records;

	public ChunkDeltaUpdateS2CPacket() {
	}

	public ChunkDeltaUpdateS2CPacket(int i, short[] ss, Chunk chunk) {
		this.pos = new ChunkPos(chunk.chunkX, chunk.chunkZ);
		this.records = new ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[i];

		for (int j = 0; j < this.records.length; j++) {
			this.records[j] = new ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord(ss[j], chunk);
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = new ChunkPos(buf.readInt(), buf.readInt());
		this.records = new ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[buf.readVarInt()];

		for (int i = 0; i < this.records.length; i++) {
			this.records[i] = new ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord(buf.readShort(), Block.BLOCK_STATES.fromId(buf.readVarInt()));
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.pos.x);
		buf.writeInt(this.pos.z);
		buf.writeVarInt(this.records.length);

		for (ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord chunkDeltaRecord : this.records) {
			buf.writeShort(chunkDeltaRecord.getPosShort());
			buf.writeVarInt(Block.BLOCK_STATES.getId(chunkDeltaRecord.getState()));
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkDeltaUpdate(this);
	}

	public ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[] getRecords() {
		return this.records;
	}

	public class ChunkDeltaRecord {
		private final short pos;
		private final BlockState state;

		public ChunkDeltaRecord(short s, BlockState blockState) {
			this.pos = s;
			this.state = blockState;
		}

		public ChunkDeltaRecord(short s, Chunk chunk) {
			this.pos = s;
			this.state = chunk.getBlockState(this.getBlockPos());
		}

		public BlockPos getBlockPos() {
			return new BlockPos(ChunkDeltaUpdateS2CPacket.this.pos.toBlockPos(this.pos >> 12 & 15, this.pos & 255, this.pos >> 8 & 15));
		}

		public short getPosShort() {
			return this.pos;
		}

		public BlockState getState() {
			return this.state;
		}
	}
}
