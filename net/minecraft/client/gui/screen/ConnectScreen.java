package net.minecraft.client.gui.screen;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectScreen extends Screen {
	private static final AtomicInteger CONNECTOR_THREADS_COUNT = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();
	private ClientConnection connection;
	private boolean connectingCancelled;
	private final Screen parent;

	public ConnectScreen(Screen screen, MinecraftClient minecraftClient, ServerInfo serverInfo) {
		this.client = minecraftClient;
		this.parent = screen;
		ServerAddress serverAddress = ServerAddress.parse(serverInfo.address);
		minecraftClient.connect(null);
		minecraftClient.setCurrentServerEntry(serverInfo);
		this.connect(serverAddress.getAddress(), serverAddress.getPort());
	}

	public ConnectScreen(Screen screen, MinecraftClient minecraftClient, String string, int i) {
		this.client = minecraftClient;
		this.parent = screen;
		minecraftClient.connect(null);
		this.connect(string, i);
	}

	private void connect(String address, int port) {
		LOGGER.info("Connecting to {}, {}", new Object[]{address, port});
		(new Thread("Server Connector #" + CONNECTOR_THREADS_COUNT.incrementAndGet()) {
				public void run() {
					InetAddress inetAddress = null;

					try {
						if (ConnectScreen.this.connectingCancelled) {
							return;
						}

						inetAddress = InetAddress.getByName(address);
						ConnectScreen.this.connection = ClientConnection.connect(inetAddress, port, ConnectScreen.this.client.options.shouldUseNativeTransport());
						ConnectScreen.this.connection
							.setPacketListener(new ClientLoginNetworkHandler(ConnectScreen.this.connection, ConnectScreen.this.client, ConnectScreen.this.parent));
						ConnectScreen.this.connection.send(new HandshakeC2SPacket(316, address, port, NetworkState.LOGIN));
						ConnectScreen.this.connection.send(new LoginHelloC2SPacket(ConnectScreen.this.client.getSession().getProfile()));
					} catch (UnknownHostException var5) {
						if (ConnectScreen.this.connectingCancelled) {
							return;
						}

						ConnectScreen.LOGGER.error("Couldn't connect to server", var5);
						ConnectScreen.this.client
							.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", "Unknown host")));
					} catch (Exception var6) {
						if (ConnectScreen.this.connectingCancelled) {
							return;
						}

						ConnectScreen.LOGGER.error("Couldn't connect to server", var6);
						String string = var6.toString();
						if (inetAddress != null) {
							String string2 = inetAddress + ":" + port;
							string = string.replaceAll(string2, "");
						}

						ConnectScreen.this.client
							.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", string)));
					}
				}
			})
			.start();
	}

	@Override
	public void tick() {
		if (this.connection != null) {
			if (this.connection.isOpen()) {
				this.connection.tick();
			} else {
				this.connection.handleDisconnection();
			}
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
	}

	@Override
	public void init() {
		this.buttons.clear();
		this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.cancel")));
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 0) {
			this.connectingCancelled = true;
			if (this.connection != null) {
				this.connection.disconnect(new LiteralText("Aborted"));
			}

			this.client.setScreen(this.parent);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		if (this.connection == null) {
			this.drawCenteredString(this.textRenderer, I18n.translate("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
		} else {
			this.drawCenteredString(this.textRenderer, I18n.translate("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
		}

		super.render(mouseX, mouseY, tickDelta);
	}
}
