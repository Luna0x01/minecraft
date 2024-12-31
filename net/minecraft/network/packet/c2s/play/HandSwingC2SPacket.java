package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class HandSwingC2SPacket implements Packet<ServerPlayPacketListener> {
	private Hand field_13812;

	public HandSwingC2SPacket() {
	}

	public HandSwingC2SPacket(Hand hand) {
		this.field_13812 = hand;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_13812 = buf.readEnumConstant(Hand.class);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.field_13812);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onHandSwing(this);
	}

	public Hand method_12707() {
		return this.field_13812;
	}
}
