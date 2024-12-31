package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class class_4384 implements Packet<ServerPlayPacketListener> {
	private int field_21576;
	private BlockPos field_21577;

	public class_4384() {
	}

	public class_4384(int i, BlockPos blockPos) {
		this.field_21576 = i;
		this.field_21577 = blockPos;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21576 = buf.readVarInt();
		this.field_21577 = buf.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21576);
		buf.writeBlockPos(this.field_21577);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_20276(this);
	}

	public int method_20287() {
		return this.field_21576;
	}

	public BlockPos method_20288() {
		return this.field_21577;
	}
}
