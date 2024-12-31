package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BlockUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private BlockPos pos;
	private BlockState state;

	public BlockUpdateS2CPacket() {
	}

	public BlockUpdateS2CPacket(BlockView blockView, BlockPos blockPos) {
		this.pos = blockPos;
		this.state = blockView.getBlockState(blockPos);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.state = Block.STATE_IDS.get(packetByteBuf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeVarInt(Block.getRawIdFromState(this.state));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockUpdate(this);
	}

	public BlockState getState() {
		return this.state;
	}

	public BlockPos getPos() {
		return this.pos;
	}
}
