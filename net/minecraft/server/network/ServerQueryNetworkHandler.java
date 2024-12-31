package net.minecraft.server.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ServerQueryNetworkHandler implements ServerQueryPacketListener {
	private static final Text field_11784 = new LiteralText("Status request has been handled.");
	private final MinecraftServer server;
	private final ClientConnection connection;
	private boolean responseSent;

	public ServerQueryNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.server = minecraftServer;
		this.connection = clientConnection;
	}

	@Override
	public void onDisconnected(Text reason) {
	}

	@Override
	public void onRequest(QueryRequestC2SPacket packet) {
		if (this.responseSent) {
			this.connection.disconnect(field_11784);
		} else {
			this.responseSent = true;
			this.connection.send(new QueryResponseS2CPacket(this.server.getServerMetadata()));
		}
	}

	@Override
	public void onPing(QueryPingC2SPacket packet) {
		this.connection.send(new QueryPongS2CPacket(packet.getStartTime()));
		this.connection.disconnect(field_11784);
	}
}
