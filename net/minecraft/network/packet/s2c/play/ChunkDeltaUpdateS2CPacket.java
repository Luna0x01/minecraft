package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.function.BiConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;

public class ChunkDeltaUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private static final int field_33341 = 12;
	private final ChunkSectionPos sectionPos;
	private final short[] positions;
	private final BlockState[] blockStates;
	private final boolean noLightingUpdates;

	public ChunkDeltaUpdateS2CPacket(ChunkSectionPos sectionPos, ShortSet positions, ChunkSection section, boolean noLightingUpdates) {
		this.sectionPos = sectionPos;
		this.noLightingUpdates = noLightingUpdates;
		int i = positions.size();
		this.positions = new short[i];
		this.blockStates = new BlockState[i];
		int j = 0;

		for (ShortIterator var7 = positions.iterator(); var7.hasNext(); j++) {
			short s = (Short)var7.next();
			this.positions[j] = s;
			this.blockStates[j] = section.getBlockState(ChunkSectionPos.unpackLocalX(s), ChunkSectionPos.unpackLocalY(s), ChunkSectionPos.unpackLocalZ(s));
		}
	}

	public ChunkDeltaUpdateS2CPacket(PacketByteBuf buf) {
		this.sectionPos = ChunkSectionPos.from(buf.readLong());
		this.noLightingUpdates = buf.readBoolean();
		int i = buf.readVarInt();
		this.positions = new short[i];
		this.blockStates = new BlockState[i];

		for (int j = 0; j < i; j++) {
			long l = buf.readVarLong();
			this.positions[j] = (short)((int)(l & 4095L));
			this.blockStates[j] = Block.STATE_IDS.get((int)(l >>> 12));
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeLong(this.sectionPos.asLong());
		buf.writeBoolean(this.noLightingUpdates);
		buf.writeVarInt(this.positions.length);

		for (int i = 0; i < this.positions.length; i++) {
			buf.writeVarLong((long)(Block.getRawIdFromState(this.blockStates[i]) << 12 | this.positions[i]));
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChunkDeltaUpdate(this);
	}

	public void visitUpdates(BiConsumer<BlockPos, BlockState> biConsumer) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < this.positions.length; i++) {
			short s = this.positions[i];
			mutable.set(this.sectionPos.unpackBlockX(s), this.sectionPos.unpackBlockY(s), this.sectionPos.unpackBlockZ(s));
			biConsumer.accept(mutable, this.blockStates[i]);
		}
	}

	public boolean shouldSkipLightingUpdates() {
		return this.noLightingUpdates;
	}
}
