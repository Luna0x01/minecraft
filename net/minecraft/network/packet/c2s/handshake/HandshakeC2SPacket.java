package net.minecraft.network.packet.c2s.handshake;

import java.io.IOException;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.util.PacketByteBuf;

public class HandshakeC2SPacket implements Packet<ServerHandshakePacketListener> {
	private int protocolVersion;
	private String address;
	private int port;
	private NetworkState state;

	public HandshakeC2SPacket() {
	}

	public HandshakeC2SPacket(int i, String string, int j, NetworkState networkState) {
		this.protocolVersion = i;
		this.address = string;
		this.port = j;
		this.state = networkState;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.protocolVersion = buf.readVarInt();
		this.address = buf.readString(255);
		this.port = buf.readUnsignedShort();
		this.state = NetworkState.byId(buf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.protocolVersion);
		buf.writeString(this.address);
		buf.writeShort(this.port);
		buf.writeVarInt(this.state.getId());
	}

	public void apply(ServerHandshakePacketListener serverHandshakePacketListener) {
		serverHandshakePacketListener.onHandshake(this);
	}

	public NetworkState getIntendedState() {
		return this.state;
	}

	public int getProtocolVersion() {
		return this.protocolVersion;
	}
}
