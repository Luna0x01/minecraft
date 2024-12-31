package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class BlockBreakingProgressS2CPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private BlockPos pos;
	private int progress;

	public BlockBreakingProgressS2CPacket() {
	}

	public BlockBreakingProgressS2CPacket(int i, BlockPos blockPos, int j) {
		this.entityId = i;
		this.pos = blockPos;
		this.progress = j;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityId = packetByteBuf.readVarInt();
		this.pos = packetByteBuf.readBlockPos();
		this.progress = packetByteBuf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeByte(this.progress);
	}

	public void method_11279(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockDestroyProgress(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getProgress() {
		return this.progress;
	}
}
