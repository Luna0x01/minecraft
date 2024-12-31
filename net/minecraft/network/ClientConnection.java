package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Lazy;
import net.minecraft.util.Tickable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ClientConnection extends SimpleChannelInboundHandler<Packet<?>> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Marker MARKER_NETWORK = MarkerManager.getMarker("NETWORK");
	public static final Marker MARKER_NETWORK_PACKETS = MarkerManager.getMarker("NETWORK_PACKETS", MARKER_NETWORK);
	public static final AttributeKey<NetworkState> ATTR_KEY_PROTOCOL = AttributeKey.valueOf("protocol");
	public static final Lazy<NioEventLoopGroup> field_11553 = new Lazy<NioEventLoopGroup>() {
		protected NioEventLoopGroup create() {
			return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
		}
	};
	public static final Lazy<EpollEventLoopGroup> field_11554 = new Lazy<EpollEventLoopGroup>() {
		protected EpollEventLoopGroup create() {
			return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
		}
	};
	public static final Lazy<LocalEventLoopGroup> field_11555 = new Lazy<LocalEventLoopGroup>() {
		protected LocalEventLoopGroup create() {
			return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
		}
	};
	private final NetworkSide side;
	private final Queue<ClientConnection.PacketWrapper> packetQueue = Queues.newConcurrentLinkedQueue();
	private final ReentrantReadWriteLock field_11557 = new ReentrantReadWriteLock();
	private Channel channel;
	private SocketAddress address;
	private PacketListener packetListener;
	private Text disconnectReason;
	private boolean encrypted;
	private boolean disconnected;

	public ClientConnection(NetworkSide networkSide) {
		this.side = networkSide;
	}

	public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
		super.channelActive(channelHandlerContext);
		this.channel = channelHandlerContext.channel();
		this.address = this.channel.remoteAddress();

		try {
			this.setState(NetworkState.HANDSHAKING);
		} catch (Throwable var3) {
			LOGGER.fatal(var3);
		}
	}

	public void setState(NetworkState state) {
		this.channel.attr(ATTR_KEY_PROTOCOL).set(state);
		this.channel.config().setAutoRead(true);
		LOGGER.debug("Enabled auto read");
	}

	public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
		this.disconnect(new TranslatableText("disconnect.endOfStream"));
	}

	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
		TranslatableText translatableText;
		if (throwable instanceof TimeoutException) {
			translatableText = new TranslatableText("disconnect.timeout");
		} else {
			translatableText = new TranslatableText("disconnect.genericReason", "Internal Exception: " + throwable);
		}

		LOGGER.debug(throwable);
		this.disconnect(translatableText);
	}

	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception {
		if (this.channel.isOpen()) {
			try {
				((Packet<PacketListener>)packet).apply(this.packetListener);
			} catch (OffThreadException var4) {
			}
		}
	}

	public void setPacketListener(PacketListener listener) {
		Validate.notNull(listener, "packetListener", new Object[0]);
		LOGGER.debug("Set listener of {} to {}", new Object[]{this, listener});
		this.packetListener = listener;
	}

	public void send(Packet<?> packet) {
		if (this.isOpen()) {
			this.sendQueuedPackets();
			this.sendImmediately(packet, null);
		} else {
			this.field_11557.writeLock().lock();

			try {
				this.packetQueue.add(new ClientConnection.PacketWrapper(packet, null));
			} finally {
				this.field_11557.writeLock().unlock();
			}
		}
	}

	public void send(
		Packet<?> packet,
		GenericFutureListener<? extends Future<? super Void>> genericFutureListener,
		GenericFutureListener<? extends Future<? super Void>>... genericFutureListeners
	) {
		if (this.isOpen()) {
			this.sendQueuedPackets();
			this.sendImmediately(packet, (GenericFutureListener<? extends Future<? super Void>>[])ArrayUtils.add(genericFutureListeners, 0, genericFutureListener));
		} else {
			this.field_11557.writeLock().lock();

			try {
				this.packetQueue
					.add(
						new ClientConnection.PacketWrapper(
							packet, (GenericFutureListener<? extends Future<? super Void>>[])ArrayUtils.add(genericFutureListeners, 0, genericFutureListener)
						)
					);
			} finally {
				this.field_11557.writeLock().unlock();
			}
		}
	}

	private void sendImmediately(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>>[] listeners) {
		final NetworkState networkState = NetworkState.getPacketHandlerState(packet);
		final NetworkState networkState2 = (NetworkState)this.channel.attr(ATTR_KEY_PROTOCOL).get();
		if (networkState2 != networkState) {
			LOGGER.debug("Disabled auto read");
			this.channel.config().setAutoRead(false);
		}

		if (this.channel.eventLoop().inEventLoop()) {
			if (networkState != networkState2) {
				this.setState(networkState);
			}

			ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
			if (listeners != null) {
				channelFuture.addListeners(listeners);
			}

			channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		} else {
			this.channel.eventLoop().execute(new Runnable() {
				public void run() {
					if (networkState != networkState2) {
						ClientConnection.this.setState(networkState);
					}

					ChannelFuture channelFuture = ClientConnection.this.channel.writeAndFlush(packet);
					if (listeners != null) {
						channelFuture.addListeners(listeners);
					}

					channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
				}
			});
		}
	}

	private void sendQueuedPackets() {
		if (this.channel != null && this.channel.isOpen()) {
			this.field_11557.readLock().lock();

			try {
				while (!this.packetQueue.isEmpty()) {
					ClientConnection.PacketWrapper packetWrapper = (ClientConnection.PacketWrapper)this.packetQueue.poll();
					this.sendImmediately(packetWrapper.packet, packetWrapper.field_8444);
				}
			} finally {
				this.field_11557.readLock().unlock();
			}
		}
	}

	public void tick() {
		this.sendQueuedPackets();
		if (this.packetListener instanceof Tickable) {
			((Tickable)this.packetListener).tick();
		}

		this.channel.flush();
	}

	public SocketAddress getAddress() {
		return this.address;
	}

	public void disconnect(Text disconnectReason) {
		if (this.channel.isOpen()) {
			this.channel.close().awaitUninterruptibly();
			this.disconnectReason = disconnectReason;
		}
	}

	public boolean isLocal() {
		return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
	}

	public static ClientConnection connect(InetAddress address, int port, boolean shouldUseNativeTransport) {
		final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
		Class<? extends SocketChannel> class_;
		Lazy<? extends EventLoopGroup> lazy;
		if (Epoll.isAvailable() && shouldUseNativeTransport) {
			class_ = EpollSocketChannel.class;
			lazy = field_11554;
		} else {
			class_ = NioSocketChannel.class;
			lazy = field_11553;
		}

		((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(lazy.get()))
					.handler(
						new ChannelInitializer<Channel>() {
							protected void initChannel(Channel channel) throws Exception {
								try {
									channel.config().setOption(ChannelOption.TCP_NODELAY, true);
								} catch (ChannelException var3) {
								}

								channel.pipeline()
									.addLast("timeout", new ReadTimeoutHandler(30))
									.addLast("splitter", new SplitterHandler())
									.addLast("decoder", new DecoderHandler(NetworkSide.CLIENTBOUND))
									.addLast("prepender", new SizePrepender())
									.addLast("encoder", new PacketEncoder(NetworkSide.SERVERBOUND))
									.addLast("packet_handler", clientConnection);
							}
						}
					))
				.channel(class_))
			.connect(address, port)
			.syncUninterruptibly();
		return clientConnection;
	}

	public static ClientConnection connectLocal(SocketAddress address) {
		final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
		((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)field_11555.get())).handler(new ChannelInitializer<Channel>() {
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline().addLast("packet_handler", clientConnection);
			}
		})).channel(LocalChannel.class)).connect(address).syncUninterruptibly();
		return clientConnection;
	}

	public void setupEncryption(SecretKey secretKey) {
		this.encrypted = true;
		this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecryptor(NetworkEncryptionUtils.cipherFromKey(2, secretKey)));
		this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncryptor(NetworkEncryptionUtils.cipherFromKey(1, secretKey)));
	}

	public boolean isEncrypted() {
		return this.encrypted;
	}

	public boolean isOpen() {
		return this.channel != null && this.channel.isOpen();
	}

	public boolean hasChannel() {
		return this.channel == null;
	}

	public PacketListener getPacketListener() {
		return this.packetListener;
	}

	public Text getDisconnectReason() {
		return this.disconnectReason;
	}

	public void disableAutoRead() {
		this.channel.config().setAutoRead(false);
	}

	public void setCompressionThreshold(int compressionThreshold) {
		if (compressionThreshold >= 0) {
			if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
				((PacketInflater)this.channel.pipeline().get("decompress")).setCompressionThreshold(compressionThreshold);
			} else {
				this.channel.pipeline().addBefore("decoder", "decompress", new PacketInflater(compressionThreshold));
			}

			if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
				((PacketDeflater)this.channel.pipeline().get("compress")).setCompressionThreshold(compressionThreshold);
			} else {
				this.channel.pipeline().addBefore("encoder", "compress", new PacketDeflater(compressionThreshold));
			}
		} else {
			if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
				this.channel.pipeline().remove("decompress");
			}

			if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
				this.channel.pipeline().remove("compress");
			}
		}
	}

	public void handleDisconnection() {
		if (this.channel != null && !this.channel.isOpen()) {
			if (this.disconnected) {
				LOGGER.warn("handleDisconnection() called twice");
			} else {
				this.disconnected = true;
				if (this.getDisconnectReason() != null) {
					this.getPacketListener().onDisconnected(this.getDisconnectReason());
				} else if (this.getPacketListener() != null) {
					this.getPacketListener().onDisconnected(new LiteralText("Disconnected"));
				}
			}
		}
	}

	static class PacketWrapper {
		private final Packet<?> packet;
		private final GenericFutureListener<? extends Future<? super Void>>[] field_8444;

		public PacketWrapper(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>>... genericFutureListeners) {
			this.packet = packet;
			this.field_8444 = genericFutureListeners;
		}
	}
}
