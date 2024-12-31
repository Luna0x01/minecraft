package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

public class ChunkDataS2CPacket implements Packet<ClientPlayPacketListener> {
	private int chunkX;
	private int chunkZ;
	private int field_13771;
	private byte[] data;
	private List<NbtCompound> blockEntities;
	private boolean isFullChunk;

	public ChunkDataS2CPacket() {
	}

	public ChunkDataS2CPacket(Chunk chunk, int i) {
		this.chunkX = chunk.chunkX;
		this.chunkZ = chunk.chunkZ;
		this.isFullChunk = i == 65535;
		boolean bl = chunk.getWorld().dimension.isOverworld();
		this.data = new byte[this.getDataSize(chunk, bl, i)];
		this.field_13771 = this.writeData(new PacketByteBuf(this.getWriteBuffer()), chunk, bl, i);
		this.blockEntities = Lists.newArrayList();

		for (Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
			BlockPos blockPos = (BlockPos)entry.getKey();
			BlockEntity blockEntity = (BlockEntity)entry.getValue();
			int j = blockPos.getY() >> 4;
			if (this.shouldLoad() || (i & 1 << j) != 0) {
				NbtCompound nbtCompound = blockEntity.getUpdatePacketContent();
				this.blockEntities.add(nbtCompound);
			}
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.chunkX = buf.readInt();
		this.chunkZ = buf.readInt();
		this.isFullChunk = buf.readBoolean();
		this.field_13771 = buf.readVarInt();
		int i = buf.readVarInt();
		if (i > 2097152) {
			throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
		} else {
			this.data = new byte[i];
			buf.readBytes(this.data);
			int j = buf.readVarInt();
			this.blockEntities = Lists.newArrayList();

			for (int k = 0; k < j; k++) {
				this.blockEntities.add(buf.readNbtCompound());
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.chunkX);
		buf.writeInt(this.chunkZ);
		buf.writeBoolean(this.isFullChunk);
		buf.writeVarInt(this.field_13771);
		buf.writeVarInt(this.data.length);
		buf.writeBytes(this.data);
		buf.writeVarInt(this.blockEntities.size());

		for (NbtCompound nbtCompound : this.blockEntities) {
			buf.writeNbtCompound(nbtCompound);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkData(this);
	}

	public PacketByteBuf getReadBuffer() {
		return new PacketByteBuf(Unpooled.wrappedBuffer(this.data));
	}

	private ByteBuf getWriteBuffer() {
		ByteBuf byteBuf = Unpooled.wrappedBuffer(this.data);
		byteBuf.writerIndex(0);
		return byteBuf;
	}

	public int writeData(PacketByteBuf buffer, Chunk chunk, boolean bl, int includedSectionsMask) {
		int i = 0;
		ChunkSection[] chunkSections = chunk.method_17003();
		int j = 0;

		for (int k = chunkSections.length; j < k; j++) {
			ChunkSection chunkSection = chunkSections[j];
			if (chunkSection != Chunk.EMPTY && (!this.shouldLoad() || !chunkSection.isEmpty()) && (includedSectionsMask & 1 << j) != 0) {
				i |= 1 << j;
				chunkSection.getBlockData().write(buffer);
				buffer.writeBytes(chunkSection.getBlockLight().getValue());
				if (bl) {
					buffer.writeBytes(chunkSection.getSkyLight().getValue());
				}
			}
		}

		if (this.shouldLoad()) {
			Biome[] biomes = chunk.method_17007();

			for (int l = 0; l < biomes.length; l++) {
				buffer.writeInt(Registry.BIOME.getRawId(biomes[l]));
			}
		}

		return i;
	}

	protected int getDataSize(Chunk chunk, boolean bl, int includedSectionsMark) {
		int i = 0;
		ChunkSection[] chunkSections = chunk.method_17003();
		int j = 0;

		for (int k = chunkSections.length; j < k; j++) {
			ChunkSection chunkSection = chunkSections[j];
			if (chunkSection != Chunk.EMPTY && (!this.shouldLoad() || !chunkSection.isEmpty()) && (includedSectionsMark & 1 << j) != 0) {
				i += chunkSection.getBlockData().packetSize();
				i += chunkSection.getBlockLight().getValue().length;
				if (bl) {
					i += chunkSection.getSkyLight().getValue().length;
				}
			}
		}

		if (this.shouldLoad()) {
			i += chunk.method_17007().length * 4;
		}

		return i;
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkZ() {
		return this.chunkZ;
	}

	public int method_7760() {
		return this.field_13771;
	}

	public boolean shouldLoad() {
		return this.isFullChunk;
	}

	public List<NbtCompound> getBlockEntityTagList() {
		return this.blockEntities;
	}
}
