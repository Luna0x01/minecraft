package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class PlayPongC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int parameter;

	public PlayPongC2SPacket(int parameter) {
		this.parameter = parameter;
	}

	public PlayPongC2SPacket(PacketByteBuf buf) {
		this.parameter = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.parameter);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPong(this);
	}

	public int getParameter() {
		return this.parameter;
	}
}
