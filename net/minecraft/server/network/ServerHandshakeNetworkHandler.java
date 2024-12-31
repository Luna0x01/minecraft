package net.minecraft.server.network;

import net.minecraft.class_4453;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ServerHandshakeNetworkHandler implements ServerHandshakePacketListener {
	private final MinecraftServer server;
	private final ClientConnection connection;

	public ServerHandshakeNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.server = minecraftServer;
		this.connection = clientConnection;
	}

	@Override
	public void onHandshake(HandshakeC2SPacket packet) {
		switch (packet.getIntendedState()) {
			case LOGIN:
				this.connection.setState(NetworkState.LOGIN);
				if (packet.getProtocolVersion() > 404) {
					Text text = new TranslatableText("multiplayer.disconnect.outdated_server", "1.13.2");
					this.connection.send(new LoginDisconnectS2CPacket(text));
					this.connection.disconnect(text);
				} else if (packet.getProtocolVersion() < 404) {
					Text text2 = new TranslatableText("multiplayer.disconnect.outdated_client", "1.13.2");
					this.connection.send(new LoginDisconnectS2CPacket(text2));
					this.connection.disconnect(text2);
				} else {
					this.connection.setPacketListener(new ServerLoginNetworkHandler(this.server, this.connection));
				}
				break;
			case STATUS:
				this.connection.setState(NetworkState.STATUS);
				this.connection.setPacketListener(new class_4453(this.server, this.connection));
				break;
			default:
				throw new UnsupportedOperationException("Invalid intention " + packet.getIntendedState());
		}
	}

	@Override
	public void onDisconnected(Text reason) {
	}
}
