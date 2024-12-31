package net.minecraft.server.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.Util;

public class QueryResponseHandler extends RconBase {
	private long lastQueryTime;
	private int queryPort;
	private final int port;
	private final int maxPlayerCount;
	private final String motd;
	private final String levelName;
	private DatagramSocket socket;
	private final byte[] packetBuffer = new byte[1460];
	private DatagramPacket currentPacket;
	private final Map<SocketAddress, String> field_2944;
	private String ip;
	private String hostname;
	private final Map<SocketAddress, QueryResponseHandler.Query> queries;
	private final long creationTime;
	private final DataStreamHelper dataStreamHelper;
	private long lastResponseTime;

	public QueryResponseHandler(DedicatedServer dedicatedServer) {
		super(dedicatedServer, "Query Listener");
		this.queryPort = dedicatedServer.getIntOrDefault("query.port", 0);
		this.hostname = dedicatedServer.getHostname();
		this.port = dedicatedServer.getPort();
		this.motd = dedicatedServer.getMotd();
		this.maxPlayerCount = dedicatedServer.getMaxPlayerCount();
		this.levelName = dedicatedServer.getLevelName();
		this.lastResponseTime = 0L;
		this.ip = "0.0.0.0";
		if (!this.hostname.isEmpty() && !this.ip.equals(this.hostname)) {
			this.ip = this.hostname;
		} else {
			this.hostname = "0.0.0.0";

			try {
				InetAddress inetAddress = InetAddress.getLocalHost();
				this.ip = inetAddress.getHostAddress();
			} catch (UnknownHostException var3) {
				this.warn("Unable to determine local host IP, please set server-ip in '" + dedicatedServer.getPropertiesFilePath() + "' : " + var3.getMessage());
			}
		}

		if (0 == this.queryPort) {
			this.queryPort = this.port;
			this.info("Setting default query port to " + this.queryPort);
			dedicatedServer.setProperty("query.port", this.queryPort);
			dedicatedServer.setProperty("debug", false);
			dedicatedServer.saveAbstractPropertiesHandler();
		}

		this.field_2944 = Maps.newHashMap();
		this.dataStreamHelper = new DataStreamHelper(1460);
		this.queries = Maps.newHashMap();
		this.creationTime = new Date().getTime();
	}

	private void reply(byte[] buf, DatagramPacket packet) throws IOException {
		this.socket.send(new DatagramPacket(buf, buf.length, packet.getSocketAddress()));
	}

	private boolean handle(DatagramPacket packet) throws IOException {
		byte[] bs = packet.getData();
		int i = packet.getLength();
		SocketAddress socketAddress = packet.getSocketAddress();
		this.log("Packet len " + i + " [" + socketAddress + "]");
		if (3 <= i && -2 == bs[0] && -3 == bs[1]) {
			this.log("Packet '" + BufferHelper.toHex(bs[2]) + "' [" + socketAddress + "]");
			switch (bs[2]) {
				case 0:
					if (!this.isValidQuery(packet)) {
						this.log("Invalid challenge [" + socketAddress + "]");
						return false;
					} else if (15 == i) {
						this.reply(this.createRulesReply(packet), packet);
						this.log("Rules [" + socketAddress + "]");
					} else {
						DataStreamHelper dataStreamHelper = new DataStreamHelper(1460);
						dataStreamHelper.write(0);
						dataStreamHelper.write(this.getMessageBytes(packet.getSocketAddress()));
						dataStreamHelper.writeBytes(this.motd);
						dataStreamHelper.writeBytes("SMP");
						dataStreamHelper.writeBytes(this.levelName);
						dataStreamHelper.writeBytes(Integer.toString(this.getCurrentPlayerCount()));
						dataStreamHelper.writeBytes(Integer.toString(this.maxPlayerCount));
						dataStreamHelper.writeShort((short)this.port);
						dataStreamHelper.writeBytes(this.ip);
						this.reply(dataStreamHelper.bytes(), packet);
						this.log("Status [" + socketAddress + "]");
					}
				default:
					return true;
				case 9:
					this.createQuery(packet);
					this.log("Challenge [" + socketAddress + "]");
					return true;
			}
		} else {
			this.log("Invalid packet [" + socketAddress + "]");
			return false;
		}
	}

	private byte[] createRulesReply(DatagramPacket packet) throws IOException {
		long l = Util.method_20227();
		if (l < this.lastResponseTime + 5000L) {
			byte[] bs = this.dataStreamHelper.bytes();
			byte[] cs = this.getMessageBytes(packet.getSocketAddress());
			bs[1] = cs[0];
			bs[2] = cs[1];
			bs[3] = cs[2];
			bs[4] = cs[3];
			return bs;
		} else {
			this.lastResponseTime = l;
			this.dataStreamHelper.reset();
			this.dataStreamHelper.write(0);
			this.dataStreamHelper.write(this.getMessageBytes(packet.getSocketAddress()));
			this.dataStreamHelper.writeBytes("splitnum");
			this.dataStreamHelper.write(128);
			this.dataStreamHelper.write(0);
			this.dataStreamHelper.writeBytes("hostname");
			this.dataStreamHelper.writeBytes(this.motd);
			this.dataStreamHelper.writeBytes("gametype");
			this.dataStreamHelper.writeBytes("SMP");
			this.dataStreamHelper.writeBytes("game_id");
			this.dataStreamHelper.writeBytes("MINECRAFT");
			this.dataStreamHelper.writeBytes("version");
			this.dataStreamHelper.writeBytes(this.server.getVersion());
			this.dataStreamHelper.writeBytes("plugins");
			this.dataStreamHelper.writeBytes(this.server.getPlugins());
			this.dataStreamHelper.writeBytes("map");
			this.dataStreamHelper.writeBytes(this.levelName);
			this.dataStreamHelper.writeBytes("numplayers");
			this.dataStreamHelper.writeBytes("" + this.getCurrentPlayerCount());
			this.dataStreamHelper.writeBytes("maxplayers");
			this.dataStreamHelper.writeBytes("" + this.maxPlayerCount);
			this.dataStreamHelper.writeBytes("hostport");
			this.dataStreamHelper.writeBytes("" + this.port);
			this.dataStreamHelper.writeBytes("hostip");
			this.dataStreamHelper.writeBytes(this.ip);
			this.dataStreamHelper.write(0);
			this.dataStreamHelper.write(1);
			this.dataStreamHelper.writeBytes("player_");
			this.dataStreamHelper.write(0);
			String[] strings = this.server.getPlayerNames();

			for (String string : strings) {
				this.dataStreamHelper.writeBytes(string);
			}

			this.dataStreamHelper.write(0);
			return this.dataStreamHelper.bytes();
		}
	}

	private byte[] getMessageBytes(SocketAddress socketAddress) {
		return ((QueryResponseHandler.Query)this.queries.get(socketAddress)).getMessageBytes();
	}

	private Boolean isValidQuery(DatagramPacket datagramPacket) {
		SocketAddress socketAddress = datagramPacket.getSocketAddress();
		if (!this.queries.containsKey(socketAddress)) {
			return false;
		} else {
			byte[] bs = datagramPacket.getData();
			return ((QueryResponseHandler.Query)this.queries.get(socketAddress)).getId() != BufferHelper.getIntBE(bs, 7, datagramPacket.getLength()) ? false : true;
		}
	}

	private void createQuery(DatagramPacket datagramPacket) throws IOException {
		QueryResponseHandler.Query query = new QueryResponseHandler.Query(datagramPacket);
		this.queries.put(datagramPacket.getSocketAddress(), query);
		this.reply(query.getReplyBuf(), datagramPacket);
	}

	private void cleanUp() {
		if (this.running) {
			long l = Util.method_20227();
			if (l >= this.lastQueryTime + 30000L) {
				this.lastQueryTime = l;
				Iterator<Entry<SocketAddress, QueryResponseHandler.Query>> iterator = this.queries.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<SocketAddress, QueryResponseHandler.Query> entry = (Entry<SocketAddress, QueryResponseHandler.Query>)iterator.next();
					if (((QueryResponseHandler.Query)entry.getValue()).startedBefore(l)) {
						iterator.remove();
					}
				}
			}
		}
	}

	public void run() {
		this.info("Query running on " + this.hostname + ":" + this.queryPort);
		this.lastQueryTime = Util.method_20227();
		this.currentPacket = new DatagramPacket(this.packetBuffer, this.packetBuffer.length);

		try {
			while (this.running) {
				try {
					this.socket.receive(this.currentPacket);
					this.cleanUp();
					this.handle(this.currentPacket);
				} catch (SocketTimeoutException var7) {
					this.cleanUp();
				} catch (PortUnreachableException var8) {
				} catch (IOException var9) {
					this.handleIoException(var9);
				}
			}
		} finally {
			this.forceClose();
		}
	}

	@Override
	public void start() {
		if (!this.running) {
			if (0 < this.queryPort && 65535 >= this.queryPort) {
				if (this.initialize()) {
					super.start();
				}
			} else {
				this.warn("Invalid query port " + this.queryPort + " found in '" + this.server.getPropertiesFilePath() + "' (queries disabled)");
			}
		}
	}

	private void handleIoException(Exception e) {
		if (this.running) {
			this.warn("Unexpected exception, buggy JRE? (" + e + ")");
			if (!this.initialize()) {
				this.logError("Failed to recover from buggy JRE, shutting down!");
				this.running = false;
			}
		}
	}

	private boolean initialize() {
		try {
			this.socket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.hostname));
			this.registerSocket(this.socket);
			this.socket.setSoTimeout(500);
			return true;
		} catch (SocketException var2) {
			this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (Socket): " + var2.getMessage());
		} catch (UnknownHostException var3) {
			this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (Unknown Host): " + var3.getMessage());
		} catch (Exception var4) {
			this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (E): " + var4.getMessage());
		}

		return false;
	}

	class Query {
		private final long startTime = new Date().getTime();
		private final int id;
		private final byte[] messageBytes;
		private final byte[] replyBuf;
		private final String message;

		public Query(DatagramPacket datagramPacket) {
			byte[] bs = datagramPacket.getData();
			this.messageBytes = new byte[4];
			this.messageBytes[0] = bs[3];
			this.messageBytes[1] = bs[4];
			this.messageBytes[2] = bs[5];
			this.messageBytes[3] = bs[6];
			this.message = new String(this.messageBytes, StandardCharsets.UTF_8);
			this.id = new Random().nextInt(16777216);
			this.replyBuf = String.format("\t%s%d\u0000", this.message, this.id).getBytes(StandardCharsets.UTF_8);
		}

		public Boolean startedBefore(long lastQueryTime) {
			return this.startTime < lastQueryTime;
		}

		public int getId() {
			return this.id;
		}

		public byte[] getReplyBuf() {
			return this.replyBuf;
		}

		public byte[] getMessageBytes() {
			return this.messageBytes;
		}
	}
}
