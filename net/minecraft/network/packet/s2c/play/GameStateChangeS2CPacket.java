package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GameStateChangeS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final String[] REASON_MESSAGES = new String[]{"tile.bed.notValid"};
	private int changeType;
	private float value;

	public GameStateChangeS2CPacket() {
	}

	public GameStateChangeS2CPacket(int i, float f) {
		this.changeType = i;
		this.value = f;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.changeType = buf.readUnsignedByte();
		this.value = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.changeType);
		buf.writeFloat(this.value);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameStateChange(this);
	}

	public int getChangeType() {
		return this.changeType;
	}

	public float getValue() {
		return this.value;
	}
}
