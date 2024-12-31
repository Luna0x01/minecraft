package net.minecraft.network.packet.s2c.play;

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
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.pos = buf.readBlockPos();
		this.progress = buf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeBlockPos(this.pos);
		buf.writeByte(this.progress);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
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
