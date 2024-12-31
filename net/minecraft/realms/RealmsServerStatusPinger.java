package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerStatusPinger {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<ClientConnection> connections = Collections.synchronizedList(Lists.newArrayList());

	public void pingServer(String address, RealmsServerPing realmsServerPing) throws UnknownHostException {
		if (address != null && !address.startsWith("0.0.0.0") && !address.isEmpty()) {
			RealmsServerAddress realmsServerAddress = RealmsServerAddress.parseString(address);
			final ClientConnection clientConnection = ClientConnection.connect(
				InetAddress.getByName(realmsServerAddress.getHost()), realmsServerAddress.getPort(), false
			);
			this.connections.add(clientConnection);
			clientConnection.setPacketListener(
				new ClientQueryPacketListener() {
					private boolean field_8369 = false;

					@Override
					public void onResponse(QueryResponseS2CPacket packet) {
						ServerMetadata serverMetadata = packet.getServerMetadata();
						if (serverMetadata.getPlayers() != null) {
							realmsServerPing.nrOfPlayers = String.valueOf(serverMetadata.getPlayers().getOnlinePlayerCount());
							if (ArrayUtils.isNotEmpty(serverMetadata.getPlayers().getSample())) {
								StringBuilder stringBuilder = new StringBuilder();

								for (GameProfile gameProfile : serverMetadata.getPlayers().getSample()) {
									if (stringBuilder.length() > 0) {
										stringBuilder.append("\n");
									}

									stringBuilder.append(gameProfile.getName());
								}

								if (serverMetadata.getPlayers().getSample().length < serverMetadata.getPlayers().getOnlinePlayerCount()) {
									if (stringBuilder.length() > 0) {
										stringBuilder.append("\n");
									}

									stringBuilder.append("... and ")
										.append(serverMetadata.getPlayers().getOnlinePlayerCount() - serverMetadata.getPlayers().getSample().length)
										.append(" more ...");
								}

								realmsServerPing.playerList = stringBuilder.toString();
							}
						} else {
							realmsServerPing.playerList = "";
						}

						clientConnection.send(new QueryPingC2SPacket(Realms.currentTimeMillis()));
						this.field_8369 = true;
					}

					@Override
					public void onPong(QueryPongS2CPacket packet) {
						clientConnection.disconnect(new LiteralText("Finished"));
					}

					@Override
					public void onDisconnected(Text reason) {
						if (!this.field_8369) {
							RealmsServerStatusPinger.LOGGER.error("Can't ping " + address + ": " + reason.asUnformattedString());
						}
					}
				}
			);

			try {
				clientConnection.send(
					new HandshakeC2SPacket(RealmsSharedConstants.NETWORK_PROTOCOL_VERSION, realmsServerAddress.getHost(), realmsServerAddress.getPort(), NetworkState.STATUS)
				);
				clientConnection.send(new QueryRequestC2SPacket());
			} catch (Throwable var6) {
				LOGGER.error(var6);
			}
		}
	}

	public void tick() {
		synchronized (this.connections) {
			Iterator<ClientConnection> iterator = this.connections.iterator();

			while (iterator.hasNext()) {
				ClientConnection clientConnection = (ClientConnection)iterator.next();
				if (clientConnection.isOpen()) {
					clientConnection.tick();
				} else {
					iterator.remove();
					clientConnection.handleDisconnection();
				}
			}
		}
	}

	public void removeAll() {
		synchronized (this.connections) {
			Iterator<ClientConnection> iterator = this.connections.iterator();

			while (iterator.hasNext()) {
				ClientConnection clientConnection = (ClientConnection)iterator.next();
				if (clientConnection.isOpen()) {
					iterator.remove();
					clientConnection.disconnect(new LiteralText("Cancelled"));
				}
			}
		}
	}
}
