package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class SwingHandC2SPacket implements Packet<ServerPlayPacketListener> {
	private Hand hand;

	public SwingHandC2SPacket() {
	}

	public SwingHandC2SPacket(Hand hand) {
		this.hand = hand;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.hand = buf.readEnumConstant(Hand.class);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.hand);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSwingHand(this);
	}

	public Hand getHand() {
		return this.hand;
	}
}
