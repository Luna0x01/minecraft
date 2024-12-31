package net.minecraft.server.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.dedicated.DedicatedServer;

public class RconServer extends RconBase {
	private int port;
	private final int defaultPort;
	private String hostname;
	private ServerSocket listener;
	private final String password;
	private Map<SocketAddress, RconClient> clients;

	public RconServer(DedicatedServer dedicatedServer) {
		super(dedicatedServer, "RCON Listener");
		this.port = dedicatedServer.getIntOrDefault("rcon.port", 0);
		this.password = dedicatedServer.getOrDefault("rcon.password", "");
		this.hostname = dedicatedServer.getHostname();
		this.defaultPort = dedicatedServer.getPort();
		if (0 == this.port) {
			this.port = this.defaultPort + 10;
			this.info("Setting default rcon port to " + this.port);
			dedicatedServer.setProperty("rcon.port", this.port);
			if (this.password.isEmpty()) {
				dedicatedServer.setProperty("rcon.password", "");
			}

			dedicatedServer.saveAbstractPropertiesHandler();
		}

		if (this.hostname.isEmpty()) {
			this.hostname = "0.0.0.0";
		}

		this.cleanClientList();
		this.listener = null;
	}

	private void cleanClientList() {
		this.clients = Maps.newHashMap();
	}

	private void removeStoppedClients() {
		Iterator<Entry<SocketAddress, RconClient>> iterator = this.clients.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<SocketAddress, RconClient> entry = (Entry<SocketAddress, RconClient>)iterator.next();
			if (!((RconClient)entry.getValue()).isRunning()) {
				iterator.remove();
			}
		}
	}

	public void run() {
		this.info("RCON running on " + this.hostname + ":" + this.port);

		try {
			while (this.running) {
				try {
					Socket socket = this.listener.accept();
					socket.setSoTimeout(500);
					RconClient rconClient = new RconClient(this.server, socket);
					rconClient.start();
					this.clients.put(socket.getRemoteSocketAddress(), rconClient);
					this.removeStoppedClients();
				} catch (SocketTimeoutException var7) {
					this.removeStoppedClients();
				} catch (IOException var8) {
					if (this.running) {
						this.info("IO: " + var8.getMessage());
					}
				}
			}
		} finally {
			this.closeSocket(this.listener);
		}
	}

	@Override
	public void start() {
		if (this.password.isEmpty()) {
			this.warn("No rcon password set in '" + this.server.getPropertiesFilePath() + "', rcon disabled!");
		} else if (0 >= this.port || 65535 < this.port) {
			this.warn("Invalid rcon port " + this.port + " found in '" + this.server.getPropertiesFilePath() + "', rcon disabled!");
		} else if (!this.running) {
			try {
				this.listener = new ServerSocket(this.port, 0, InetAddress.getByName(this.hostname));
				this.listener.setSoTimeout(500);
				super.start();
			} catch (IOException var2) {
				this.warn("Unable to initialise rcon on " + this.hostname + ":" + this.port + " : " + var2.getMessage());
			}
		}
	}
}
