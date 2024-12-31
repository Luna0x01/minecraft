package net.minecraft.server.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

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
				if (packet.getProtocolVersion() > 316) {
					LiteralText literalText = new LiteralText("Outdated server! I'm still on 1.11.2");
					this.connection.send(new LoginDisconnectS2CPacket(literalText));
					this.connection.disconnect(literalText);
				} else if (packet.getProtocolVersion() < 316) {
					LiteralText literalText2 = new LiteralText("Outdated client! Please use 1.11.2");
					this.connection.send(new LoginDisconnectS2CPacket(literalText2));
					this.connection.disconnect(literalText2);
				} else {
					this.connection.setPacketListener(new ServerLoginNetworkHandler(this.server, this.connection));
				}
				break;
			case STATUS:
				this.connection.setState(NetworkState.STATUS);
				this.connection.setPacketListener(new ServerQueryNetworkHandler(this.server, this.connection));
				break;
			default:
				throw new UnsupportedOperationException("Invalid intention " + packet.getIntendedState());
		}
	}

	@Override
	public void onDisconnected(Text reason) {
	}
}
