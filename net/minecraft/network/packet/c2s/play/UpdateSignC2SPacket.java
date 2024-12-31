package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class UpdateSignC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos signPos;
	private Text[] text;

	public UpdateSignC2SPacket() {
	}

	public UpdateSignC2SPacket(BlockPos blockPos, Text[] texts) {
		this.signPos = blockPos;
		this.text = new Text[]{texts[0], texts[1], texts[2], texts[3]};
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.signPos = buf.readBlockPos();
		this.text = new Text[4];

		for (int i = 0; i < 4; i++) {
			String string = buf.readString(384);
			Text text = Text.Serializer.deserialize(string);
			this.text[i] = text;
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.signPos);

		for (int i = 0; i < 4; i++) {
			Text text = this.text[i];
			String string = Text.Serializer.serialize(text);
			buf.writeString(string);
		}
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSignUpdate(this);
	}

	public BlockPos getSignPos() {
		return this.signPos;
	}

	public Text[] getText() {
		return this.text;
	}
}
