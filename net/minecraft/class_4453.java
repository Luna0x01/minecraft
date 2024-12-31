package net.minecraft;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class class_4453 implements ServerQueryPacketListener {
	private static final Text field_21881 = new TranslatableText("multiplayer.status.request_handled");
	private final MinecraftServer field_21882;
	private final ClientConnection field_21883;
	private boolean field_21884;

	public class_4453(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.field_21882 = minecraftServer;
		this.field_21883 = clientConnection;
	}

	@Override
	public void onDisconnected(Text reason) {
	}

	@Override
	public void onRequest(QueryRequestC2SPacket packet) {
		if (this.field_21884) {
			this.field_21883.disconnect(field_21881);
		} else {
			this.field_21884 = true;
			this.field_21883.send(new QueryResponseS2CPacket(this.field_21882.getServerMetadata()));
		}
	}

	@Override
	public void onPing(QueryPingC2SPacket packet) {
		this.field_21883.send(new QueryPongS2CPacket(packet.getStartTime()));
		this.field_21883.disconnect(field_21881);
	}
}
