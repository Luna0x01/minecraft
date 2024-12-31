package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ServerHandshakeNetworkHandler implements ServerHandshakePacketListener {
	private final MinecraftServer server;
	private final ClientConnection client;

	public ServerHandshakeNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.server = minecraftServer;
		this.client = clientConnection;
	}

	@Override
	public void onHandshake(HandshakeC2SPacket handshakeC2SPacket) {
		switch (handshakeC2SPacket.getIntendedState()) {
			case field_20593:
				this.client.setState(NetworkState.field_20593);
				if (handshakeC2SPacket.getProtocolVersion() > SharedConstants.getGameVersion().getProtocolVersion()) {
					Text text = new TranslatableText("multiplayer.disconnect.outdated_server", SharedConstants.getGameVersion().getName());
					this.client.send(new LoginDisconnectS2CPacket(text));
					this.client.disconnect(text);
				} else if (handshakeC2SPacket.getProtocolVersion() < SharedConstants.getGameVersion().getProtocolVersion()) {
					Text text2 = new TranslatableText("multiplayer.disconnect.outdated_client", SharedConstants.getGameVersion().getName());
					this.client.send(new LoginDisconnectS2CPacket(text2));
					this.client.disconnect(text2);
				} else {
					this.client.setPacketListener(new ServerLoginNetworkHandler(this.server, this.client));
				}
				break;
			case field_20592:
				this.client.setState(NetworkState.field_20592);
				this.client.setPacketListener(new ServerQueryNetworkHandler(this.server, this.client));
				break;
			default:
				throw new UnsupportedOperationException("Invalid intention " + handshakeC2SPacket.getIntendedState());
		}
	}

	@Override
	public void onDisconnected(Text text) {
	}

	@Override
	public ClientConnection getConnection() {
		return this.client;
	}
}
