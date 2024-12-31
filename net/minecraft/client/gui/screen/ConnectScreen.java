package net.minecraft.client.gui.screen;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.class_4325;
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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectScreen extends Screen {
	private static final AtomicInteger CONNECTOR_THREADS_COUNT = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();
	private ClientConnection connection;
	private boolean connectingCancelled;
	private final Screen parent;
	private Text field_20238 = new TranslatableText("connect.connecting");

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
		LOGGER.info("Connecting to {}, {}", address, port);
		Thread thread = new Thread("Server Connector #" + CONNECTOR_THREADS_COUNT.incrementAndGet()) {
			public void run() {
				InetAddress inetAddress = null;

				try {
					if (ConnectScreen.this.connectingCancelled) {
						return;
					}

					inetAddress = InetAddress.getByName(address);
					ConnectScreen.this.connection = ClientConnection.connect(inetAddress, port, ConnectScreen.this.client.options.shouldUseNativeTransport());
					ConnectScreen.this.connection
						.setPacketListener(
							new ClientLoginNetworkHandler(
								ConnectScreen.this.connection, ConnectScreen.this.client, ConnectScreen.this.parent, text -> ConnectScreen.this.method_18556(text)
							)
						);
					ConnectScreen.this.connection.send(new HandshakeC2SPacket(address, port, NetworkState.LOGIN));
					ConnectScreen.this.connection.send(new LoginHelloC2SPacket(ConnectScreen.this.client.getSession().getProfile()));
				} catch (UnknownHostException var4) {
					if (ConnectScreen.this.connectingCancelled) {
						return;
					}

					ConnectScreen.LOGGER.error("Couldn't connect to server", var4);
					ConnectScreen.this.client
						.submit(
							() -> ConnectScreen.this.client
									.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", "Unknown host")))
						);
				} catch (Exception var5) {
					if (ConnectScreen.this.connectingCancelled) {
						return;
					}

					ConnectScreen.LOGGER.error("Couldn't connect to server", var5);
					String string = inetAddress == null ? var5.toString() : var5.toString().replaceAll(inetAddress + ":" + port, "");
					ConnectScreen.this.client
						.submit(
							() -> ConnectScreen.this.client
									.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", string)))
						);
				}
			}
		};
		thread.setUncaughtExceptionHandler(new class_4325(LOGGER));
		thread.start();
	}

	private void method_18556(Text text) {
		this.field_20238 = text;
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
	public boolean method_18607() {
		return false;
	}

	@Override
	protected void init() {
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				ConnectScreen.this.connectingCancelled = true;
				if (ConnectScreen.this.connection != null) {
					ConnectScreen.this.connection.disconnect(new TranslatableText("connect.aborted"));
				}

				ConnectScreen.this.client.setScreen(ConnectScreen.this.parent);
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.field_20238.asFormattedString(), this.width / 2, this.height / 2 - 50, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}
}
