package net.minecraft.server.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.class_4336;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RconBase implements Runnable {
	private static final Logger field_21948 = LogManager.getLogger();
	private static final AtomicInteger field_9022 = new AtomicInteger(0);
	protected boolean running;
	protected DedicatedServer server;
	protected final String description;
	protected Thread rconThread;
	protected int field_2932 = 5;
	protected List<DatagramSocket> sockets = Lists.newArrayList();
	protected List<ServerSocket> serverSockets = Lists.newArrayList();

	protected RconBase(DedicatedServer dedicatedServer, String string) {
		this.server = dedicatedServer;
		this.description = string;
		if (this.server.isDebuggingEnabled()) {
			this.warn("Debugging is enabled, performance maybe reduced!");
		}
	}

	public synchronized void start() {
		this.rconThread = new Thread(this, this.description + " #" + field_9022.incrementAndGet());
		this.rconThread.setUncaughtExceptionHandler(new class_4336(field_21948));
		this.rconThread.start();
		this.running = true;
	}

	public boolean isRunning() {
		return this.running;
	}

	protected void log(String message) {
		this.server.log(message);
	}

	protected void info(String message) {
		this.server.logInfo(message);
	}

	protected void warn(String message) {
		this.server.logWarn(message);
	}

	protected void logError(String message) {
		this.server.logError(message);
	}

	protected int getCurrentPlayerCount() {
		return this.server.getCurrentPlayerCount();
	}

	protected void registerSocket(DatagramSocket datagramSocket) {
		this.log("registerSocket: " + datagramSocket);
		this.sockets.add(datagramSocket);
	}

	protected boolean closeSocket(DatagramSocket socket, boolean remove) {
		this.log("closeSocket: " + socket);
		if (null == socket) {
			return false;
		} else {
			boolean bl = false;
			if (!socket.isClosed()) {
				socket.close();
				bl = true;
			}

			if (remove) {
				this.sockets.remove(socket);
			}

			return bl;
		}
	}

	protected boolean closeSocket(ServerSocket socket) {
		return this.closeSocket(socket, true);
	}

	protected boolean closeSocket(ServerSocket socket, boolean remove) {
		this.log("closeSocket: " + socket);
		if (null == socket) {
			return false;
		} else {
			boolean bl = false;

			try {
				if (!socket.isClosed()) {
					socket.close();
					bl = true;
				}
			} catch (IOException var5) {
				this.warn("IO: " + var5.getMessage());
			}

			if (remove) {
				this.serverSockets.remove(socket);
			}

			return bl;
		}
	}

	protected void forceClose() {
		this.forceClose(false);
	}

	protected void forceClose(boolean warn) {
		int i = 0;

		for (DatagramSocket datagramSocket : this.sockets) {
			if (this.closeSocket(datagramSocket, false)) {
				i++;
			}
		}

		this.sockets.clear();

		for (ServerSocket serverSocket : this.serverSockets) {
			if (this.closeSocket(serverSocket, false)) {
				i++;
			}
		}

		this.serverSockets.clear();
		if (warn && 0 < i) {
			this.warn("Force closed " + i + " sockets");
		}
	}
}
