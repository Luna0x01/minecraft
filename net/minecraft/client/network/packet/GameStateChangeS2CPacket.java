package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GameStateChangeS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final String[] REASON_MESSAGES = new String[]{"block.minecraft.bed.not_valid"};
	private int reason;
	private float value;

	public GameStateChangeS2CPacket() {
	}

	public GameStateChangeS2CPacket(int i, float f) {
		this.reason = i;
		this.value = f;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.reason = packetByteBuf.readUnsignedByte();
		this.value = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.reason);
		packetByteBuf.writeFloat(this.value);
	}

	public void method_11490(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameStateChange(this);
	}

	public int getReason() {
		return this.reason;
	}

	public float getValue() {
		return this.value;
	}
}
