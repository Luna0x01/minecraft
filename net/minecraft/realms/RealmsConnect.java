package net.minecraft.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConnect {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RealmsScreen onlineScreen;
	private volatile boolean aborted = false;
	private ClientConnection connection;

	public RealmsConnect(RealmsScreen realmsScreen) {
		this.onlineScreen = realmsScreen;
	}

	public void connect(String string, int i) {
		Realms.setConnectedToRealms(true);
		(new Thread("Realms-connect-task") {
				public void run() {
					InetAddress inetAddress = null;

					try {
						inetAddress = InetAddress.getByName(string);
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.this.connection = ClientConnection.connect(inetAddress, i, MinecraftClient.getInstance().options.shouldUseNativeTransport());
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.this.connection
							.setPacketListener(
								new ClientLoginNetworkHandler(RealmsConnect.this.connection, MinecraftClient.getInstance(), RealmsConnect.this.onlineScreen.getProxy())
							);
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.this.connection.send(new HandshakeC2SPacket(47, string, i, NetworkState.LOGIN));
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.this.connection.send(new LoginHelloC2SPacket(MinecraftClient.getInstance().getSession().getProfile()));
					} catch (UnknownHostException var5) {
						Realms.clearResourcePack();
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.LOGGER.error("Couldn't connect to world", var5);
						MinecraftClient.getInstance().getResourcePackLoader().clear();
						Realms.setScreen(
							new DisconnectedRealmsScreen(
								RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableText("disconnect.genericReason", "Unknown host '" + string + "'")
							)
						);
					} catch (Exception var6) {
						Realms.clearResourcePack();
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.LOGGER.error("Couldn't connect to world", var6);
						String string = var6.toString();
						if (inetAddress != null) {
							String string2 = inetAddress.toString() + ":" + i;
							string = string.replaceAll(string2, "");
						}

						Realms.setScreen(
							new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableText("disconnect.genericReason", string))
						);
					}
				}
			})
			.start();
	}

	public void abort() {
		this.aborted = true;
	}

	public void tick() {
		if (this.connection != null) {
			if (this.connection.isOpen()) {
				this.connection.tick();
			} else {
				this.connection.handleDisconnection();
			}
		}
	}
}
