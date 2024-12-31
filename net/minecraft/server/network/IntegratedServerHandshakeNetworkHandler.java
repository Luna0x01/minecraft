package net.minecraft.server.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.text.Text;

public class IntegratedServerHandshakeNetworkHandler implements ServerHandshakePacketListener {
	private final MinecraftServer server;
	private final ClientConnection client;

	public IntegratedServerHandshakeNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.server = minecraftServer;
		this.client = clientConnection;
	}

	@Override
	public void onHandshake(HandshakeC2SPacket handshakeC2SPacket) {
		this.client.setState(handshakeC2SPacket.getIntendedState());
		this.client.setPacketListener(new ServerLoginNetworkHandler(this.server, this.client));
	}

	@Override
	public void onDisconnected(Text text) {
	}

	@Override
	public ClientConnection getConnection() {
		return this.client;
	}
}
