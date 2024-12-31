package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class UpdateSignC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos signPos;
	private String[] text;

	public UpdateSignC2SPacket() {
	}

	public UpdateSignC2SPacket(BlockPos blockPos, Text text, Text text2, Text text3, Text text4) {
		this.signPos = blockPos;
		this.text = new String[]{text.getString(), text2.getString(), text3.getString(), text4.getString()};
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.signPos = buf.readBlockPos();
		this.text = new String[4];

		for (int i = 0; i < 4; i++) {
			this.text[i] = buf.readString(384);
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.signPos);

		for (int i = 0; i < 4; i++) {
			buf.writeString(this.text[i]);
		}
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSignUpdate(this);
	}

	public BlockPos getSignPos() {
		return this.signPos;
	}

	public String[] method_10729() {
		return this.text;
	}
}
