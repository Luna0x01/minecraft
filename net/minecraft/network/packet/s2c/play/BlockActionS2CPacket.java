package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class BlockActionS2CPacket implements Packet<ClientPlayPacketListener> {
	private BlockPos pos;
	private int type;
	private int data;
	private Block block;

	public BlockActionS2CPacket() {
	}

	public BlockActionS2CPacket(BlockPos blockPos, Block block, int i, int j) {
		this.pos = blockPos;
		this.type = i;
		this.data = j;
		this.block = block;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.type = buf.readUnsignedByte();
		this.data = buf.readUnsignedByte();
		this.block = Block.getById(buf.readVarInt() & 4095);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
		buf.writeByte(this.type);
		buf.writeByte(this.data);
		buf.writeVarInt(Block.getIdByBlock(this.block) & 4095);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockAction(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getType() {
		return this.type;
	}

	public int getData() {
		return this.data;
	}

	public Block getBlock() {
		return this.block;
	}
}
