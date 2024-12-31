package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

public class ChunkDataS2CPacket implements Packet<ClientPlayPacketListener> {
	private int chunkX;
	private int chunkZ;
	private ChunkDataS2CPacket.ExtraData extraData;
	private boolean isFullChunk;

	public ChunkDataS2CPacket() {
	}

	public ChunkDataS2CPacket(Chunk chunk, boolean bl, int i) {
		this.chunkX = chunk.chunkX;
		this.chunkZ = chunk.chunkZ;
		this.isFullChunk = bl;
		this.extraData = createExtraData(chunk, bl, !chunk.getWorld().dimension.hasNoSkylight(), i);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.chunkX = buf.readInt();
		this.chunkZ = buf.readInt();
		this.isFullChunk = buf.readBoolean();
		this.extraData = new ChunkDataS2CPacket.ExtraData();
		this.extraData.size = buf.readShort();
		this.extraData.bytes = buf.readByteArray();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.chunkX);
		buf.writeInt(this.chunkZ);
		buf.writeBoolean(this.isFullChunk);
		buf.writeShort((short)(this.extraData.size & 65535));
		buf.writeByteArray(this.extraData.bytes);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkData(this);
	}

	public byte[] method_7757() {
		return this.extraData.bytes;
	}

	protected static int method_10659(int i, boolean bl, boolean bl2) {
		int j = i * 2 * 16 * 16 * 16;
		int k = i * 16 * 16 * 16 / 2;
		int l = bl ? i * 16 * 16 * 16 / 2 : 0;
		int m = bl2 ? 256 : 0;
		return j + k + l + m;
	}

	public static ChunkDataS2CPacket.ExtraData createExtraData(Chunk chunk, boolean load, boolean notNether, int i) {
		ChunkSection[] chunkSections = chunk.getBlockStorage();
		ChunkDataS2CPacket.ExtraData extraData = new ChunkDataS2CPacket.ExtraData();
		List<ChunkSection> list = Lists.newArrayList();

		for (int j = 0; j < chunkSections.length; j++) {
			ChunkSection chunkSection = chunkSections[j];
			if (chunkSection != null && (!load || !chunkSection.isEmpty()) && (i & 1 << j) != 0) {
				extraData.size |= 1 << j;
				list.add(chunkSection);
			}
		}

		extraData.bytes = new byte[method_10659(Integer.bitCount(extraData.size), notNether, load)];
		int k = 0;

		for (ChunkSection chunkSection2 : list) {
			char[] cs = chunkSection2.getBlockStates();

			for (char c : cs) {
				extraData.bytes[k++] = (byte)(c & 255);
				extraData.bytes[k++] = (byte)(c >> '\b' & 0xFF);
			}
		}

		for (ChunkSection chunkSection3 : list) {
			k = method_10661(chunkSection3.getBlockLight().getValue(), extraData.bytes, k);
		}

		if (notNether) {
			for (ChunkSection chunkSection4 : list) {
				k = method_10661(chunkSection4.getSkyLight().getValue(), extraData.bytes, k);
			}
		}

		if (load) {
			method_10661(chunk.getBiomeArray(), extraData.bytes, k);
		}

		return extraData;
	}

	private static int method_10661(byte[] bs, byte[] cs, int i) {
		System.arraycopy(bs, 0, cs, i, bs.length);
		return i + bs.length;
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkZ() {
		return this.chunkZ;
	}

	public int method_7760() {
		return this.extraData.size;
	}

	public boolean shouldLoad() {
		return this.isFullChunk;
	}

	public static class ExtraData {
		public byte[] bytes;
		public int size;
	}
}
