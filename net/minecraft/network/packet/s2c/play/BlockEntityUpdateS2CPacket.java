package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class BlockEntityUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private BlockPos pos;
	private int blockEntityId;
	private NbtCompound nbt;

	public BlockEntityUpdateS2CPacket() {
	}

	public BlockEntityUpdateS2CPacket(BlockPos blockPos, int i, NbtCompound nbtCompound) {
		this.pos = blockPos;
		this.blockEntityId = i;
		this.nbt = nbtCompound;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.blockEntityId = buf.readUnsignedByte();
		this.nbt = buf.readNbtCompound();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
		buf.writeByte((byte)this.blockEntityId);
		buf.writeNbtCompound(this.nbt);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockEntityUpdate(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getBlockEntityId() {
		return this.blockEntityId;
	}

	public NbtCompound getNbt() {
		return this.nbt;
	}
}
