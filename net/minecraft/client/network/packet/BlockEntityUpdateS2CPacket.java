package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class BlockEntityUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private BlockPos pos;
	private int blockEntityType;
	private CompoundTag tag;

	public BlockEntityUpdateS2CPacket() {
	}

	public BlockEntityUpdateS2CPacket(BlockPos blockPos, int i, CompoundTag compoundTag) {
		this.pos = blockPos;
		this.blockEntityType = i;
		this.tag = compoundTag;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.blockEntityType = packetByteBuf.readUnsignedByte();
		this.tag = packetByteBuf.readCompoundTag();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeByte((byte)this.blockEntityType);
		packetByteBuf.writeCompoundTag(this.tag);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockEntityUpdate(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getBlockEntityType() {
		return this.blockEntityType;
	}

	public CompoundTag getCompoundTag() {
		return this.tag;
	}
}
