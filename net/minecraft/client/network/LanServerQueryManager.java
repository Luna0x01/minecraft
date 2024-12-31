package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.LanServerPinger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanServerQueryManager {
	private static final AtomicInteger THREAD_ID = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();

	public static class LanServerDetector extends Thread {
		private final LanServerQueryManager.LanServerEntryList entryList;
		private final InetAddress multicastAddress;
		private final MulticastSocket socket;

		public LanServerDetector(LanServerQueryManager.LanServerEntryList lanServerEntryList) throws IOException {
			super("LanServerDetector #" + LanServerQueryManager.THREAD_ID.incrementAndGet());
			this.entryList = lanServerEntryList;
			this.setDaemon(true);
			this.socket = new MulticastSocket(4445);
			this.multicastAddress = InetAddress.getByName("224.0.2.60");
			this.socket.setSoTimeout(5000);
			this.socket.joinGroup(this.multicastAddress);
		}

		public void run() {
			byte[] bs = new byte[1024];

			while (!this.isInterrupted()) {
				DatagramPacket datagramPacket = new DatagramPacket(bs, bs.length);

				try {
					this.socket.receive(datagramPacket);
				} catch (SocketTimeoutException var5) {
					continue;
				} catch (IOException var6) {
					LanServerQueryManager.LOGGER.error("Couldn't ping server", var6);
					break;
				}

				String string = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
				LanServerQueryManager.LOGGER.debug(datagramPacket.getAddress() + ": " + string);
				this.entryList.addServer(string, datagramPacket.getAddress());
			}

			try {
				this.socket.leaveGroup(this.multicastAddress);
			} catch (IOException var4) {
			}

			this.socket.close();
		}
	}

	public static class LanServerEntryList {
		private List<LanServerQueryManager.LanServerInfo> serverEntries = Lists.newArrayList();
		boolean dirty;

		public synchronized boolean needsUpdate() {
			return this.dirty;
		}

		public synchronized void markClean() {
			this.dirty = false;
		}

		public synchronized List<LanServerQueryManager.LanServerInfo> getServers() {
			return Collections.unmodifiableList(this.serverEntries);
		}

		public synchronized void addServer(String name, InetAddress address) {
			String string = LanServerPinger.parseAnnouncementMotd(name);
			String string2 = LanServerPinger.parseAnnouncementAddressPort(name);
			if (string2 != null) {
				string2 = address.getHostAddress() + ":" + string2;
				boolean bl = false;

				for (LanServerQueryManager.LanServerInfo lanServerInfo : this.serverEntries) {
					if (lanServerInfo.getAddressPort().equals(string2)) {
						lanServerInfo.updateLastTime();
						bl = true;
						break;
					}
				}

				if (!bl) {
					this.serverEntries.add(new LanServerQueryManager.LanServerInfo(string, string2));
					this.dirty = true;
				}
			}
		}
	}

	public static class LanServerInfo {
		private String motd;
		private String addressPort;
		private long lastTimeMillis;

		public LanServerInfo(String string, String string2) {
			this.motd = string;
			this.addressPort = string2;
			this.lastTimeMillis = MinecraftClient.getTime();
		}

		public String getMotd() {
			return this.motd;
		}

		public String getAddressPort() {
			return this.addressPort;
		}

		public void updateLastTime() {
			this.lastTimeMillis = MinecraftClient.getTime();
		}
	}
}
