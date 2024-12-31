package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiplayerServerListPinger {
	private static final Splitter ZERO_SPLITTER = Splitter.on('\u0000').limit(6);
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<ClientConnection> clientConnections = Collections.synchronizedList(Lists.newArrayList());

	public void add(ServerInfo entry) throws UnknownHostException {
		ServerAddress serverAddress = ServerAddress.parse(entry.address);
		final ClientConnection clientConnection = ClientConnection.connect(InetAddress.getByName(serverAddress.getAddress()), serverAddress.getPort(), false);
		this.clientConnections.add(clientConnection);
		entry.label = "Pinging...";
		entry.ping = -1L;
		entry.playerListSummary = null;
		clientConnection.setPacketListener(
			new ClientQueryPacketListener() {
				private boolean sentQuery;
				private boolean received;
				private long startTime;

				@Override
				public void onResponse(QueryResponseS2CPacket packet) {
					if (this.received) {
						clientConnection.disconnect(new LiteralText("Received unrequested status"));
					} else {
						this.received = true;
						ServerMetadata serverMetadata = packet.getServerMetadata();
						if (serverMetadata.getDescription() != null) {
							entry.label = serverMetadata.getDescription().asFormattedString();
						} else {
							entry.label = "";
						}

						if (serverMetadata.getVersion() != null) {
							entry.version = serverMetadata.getVersion().getGameVersion();
							entry.protocolVersion = serverMetadata.getVersion().getProtocolVersion();
						} else {
							entry.version = "Old";
							entry.protocolVersion = 0;
						}

						if (serverMetadata.getPlayers() != null) {
							entry.playerCountLabel = Formatting.GRAY
								+ ""
								+ serverMetadata.getPlayers().getOnlinePlayerCount()
								+ ""
								+ Formatting.DARK_GRAY
								+ "/"
								+ Formatting.GRAY
								+ serverMetadata.getPlayers().getPlayerLimit();
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

								entry.playerListSummary = stringBuilder.toString();
							}
						} else {
							entry.playerCountLabel = Formatting.DARK_GRAY + "???";
						}

						if (serverMetadata.getFavicon() != null) {
							String string = serverMetadata.getFavicon();
							if (string.startsWith("data:image/png;base64,")) {
								entry.setIcon(string.substring("data:image/png;base64,".length()));
							} else {
								MultiplayerServerListPinger.LOGGER.error("Invalid server icon (unknown format)");
							}
						} else {
							entry.setIcon(null);
						}

						this.startTime = MinecraftClient.getTime();
						clientConnection.send(new QueryPingC2SPacket(this.startTime));
						this.sentQuery = true;
					}
				}

				@Override
				public void onPong(QueryPongS2CPacket packet) {
					long l = this.startTime;
					long m = MinecraftClient.getTime();
					entry.ping = m - l;
					clientConnection.disconnect(new LiteralText("Finished"));
				}

				@Override
				public void onDisconnected(Text reason) {
					if (!this.sentQuery) {
						MultiplayerServerListPinger.LOGGER.error("Can't ping {}: {}", new Object[]{entry.address, reason.asUnformattedString()});
						entry.label = Formatting.DARK_RED + "Can't connect to server.";
						entry.playerCountLabel = "";
						MultiplayerServerListPinger.this.ping(entry);
					}
				}
			}
		);

		try {
			clientConnection.send(new HandshakeC2SPacket(210, serverAddress.getAddress(), serverAddress.getPort(), NetworkState.STATUS));
			clientConnection.send(new QueryRequestC2SPacket());
		} catch (Throwable var5) {
			LOGGER.error(var5);
		}
	}

	private void ping(ServerInfo serverInfo) {
		final ServerAddress serverAddress = ServerAddress.parse(serverInfo.address);
		((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)ClientConnection.field_11553.get())).handler(new ChannelInitializer<Channel>() {
			protected void initChannel(Channel channel) throws Exception {
				try {
					channel.config().setOption(ChannelOption.TCP_NODELAY, true);
				} catch (ChannelException var3) {
				}

				channel.pipeline().addLast(new ChannelHandler[]{new SimpleChannelInboundHandler<ByteBuf>() {
					public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
						super.channelActive(channelHandlerContext);
						ByteBuf byteBuf = Unpooled.buffer();

						try {
							byteBuf.writeByte(254);
							byteBuf.writeByte(1);
							byteBuf.writeByte(250);
							char[] cs = "MC|PingHost".toCharArray();
							byteBuf.writeShort(cs.length);

							for (char c : cs) {
								byteBuf.writeChar(c);
							}

							byteBuf.writeShort(7 + 2 * serverAddress.getAddress().length());
							byteBuf.writeByte(127);
							cs = serverAddress.getAddress().toCharArray();
							byteBuf.writeShort(cs.length);

							for (char d : cs) {
								byteBuf.writeChar(d);
							}

							byteBuf.writeInt(serverAddress.getPort());
							channelHandlerContext.channel().writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
						} finally {
							byteBuf.release();
						}
					}

					protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
						short s = byteBuf.readUnsignedByte();
						if (s == 255) {
							String string = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), Charsets.UTF_16BE);
							String[] strings = (String[])Iterables.toArray(MultiplayerServerListPinger.ZERO_SPLITTER.split(string), String.class);
							if ("§1".equals(strings[0])) {
								int i = MathHelper.parseInt(strings[1], 0);
								String string2 = strings[2];
								String string3 = strings[3];
								int j = MathHelper.parseInt(strings[4], -1);
								int k = MathHelper.parseInt(strings[5], -1);
								serverInfo.protocolVersion = -1;
								serverInfo.version = string2;
								serverInfo.label = string3;
								serverInfo.playerCountLabel = Formatting.GRAY + "" + j + "" + Formatting.DARK_GRAY + "/" + Formatting.GRAY + k;
							}
						}

						channelHandlerContext.close();
					}

					public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
						channelHandlerContext.close();
					}
				}});
			}
		})).channel(NioSocketChannel.class)).connect(serverAddress.getAddress(), serverAddress.getPort());
	}

	public void tick() {
		synchronized (this.clientConnections) {
			Iterator<ClientConnection> iterator = this.clientConnections.iterator();

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

	public void cancel() {
		synchronized (this.clientConnections) {
			Iterator<ClientConnection> iterator = this.clientConnections.iterator();

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
