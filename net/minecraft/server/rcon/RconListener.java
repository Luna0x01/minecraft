package net.minecraft.server.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RconListener extends RconBase {
	private static final Logger SERVER_LOGGER = LogManager.getLogger();
	private final ServerSocket listener;
	private final String password;
	private final List<RconClient> clients = Lists.newArrayList();
	private final DedicatedServer server;

	private RconListener(DedicatedServer server, ServerSocket listener, String password) {
		super("RCON Listener");
		this.server = server;
		this.listener = listener;
		this.password = password;
	}

	private void removeStoppedClients() {
		this.clients.removeIf(client -> !client.isRunning());
	}

	public void run() {
		try {
			while (this.running) {
				try {
					Socket socket = this.listener.accept();
					RconClient rconClient = new RconClient(this.server, this.password, socket);
					rconClient.start();
					this.clients.add(rconClient);
					this.removeStoppedClients();
				} catch (SocketTimeoutException var7) {
					this.removeStoppedClients();
				} catch (IOException var8) {
					if (this.running) {
						SERVER_LOGGER.info("IO exception: ", var8);
					}
				}
			}
		} finally {
			this.closeSocket(this.listener);
		}
	}

	@Nullable
	public static RconListener create(DedicatedServer server) {
		ServerPropertiesHandler serverPropertiesHandler = server.getProperties();
		String string = server.getHostname();
		if (string.isEmpty()) {
			string = "0.0.0.0";
		}

		int i = serverPropertiesHandler.rconPort;
		if (0 < i && 65535 >= i) {
			String string2 = serverPropertiesHandler.rconPassword;
			if (string2.isEmpty()) {
				SERVER_LOGGER.warn("No rcon password set in server.properties, rcon disabled!");
				return null;
			} else {
				try {
					ServerSocket serverSocket = new ServerSocket(i, 0, InetAddress.getByName(string));
					serverSocket.setSoTimeout(500);
					RconListener rconListener = new RconListener(server, serverSocket, string2);
					if (!rconListener.start()) {
						return null;
					} else {
						SERVER_LOGGER.info("RCON running on {}:{}", string, i);
						return rconListener;
					}
				} catch (IOException var7) {
					SERVER_LOGGER.warn("Unable to initialise RCON on {}:{}", string, i, var7);
					return null;
				}
			}
		} else {
			SERVER_LOGGER.warn("Invalid rcon port {} found in server.properties, rcon disabled!", i);
			return null;
		}
	}

	@Override
	public void stop() {
		this.running = false;
		this.closeSocket(this.listener);
		super.stop();

		for (RconClient rconClient : this.clients) {
			if (rconClient.isRunning()) {
				rconClient.stop();
			}
		}

		this.clients.clear();
	}

	private void closeSocket(ServerSocket socket) {
		SERVER_LOGGER.debug("closeSocket: {}", socket);

		try {
			socket.close();
		} catch (IOException var3) {
			SERVER_LOGGER.warn("Failed to close socket", var3);
		}
	}
}
