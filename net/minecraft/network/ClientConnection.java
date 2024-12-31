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
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
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
import net.minecraft.class_4375;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Lazy;
import net.minecraft.util.Tickable;
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
	public static final Lazy<NioEventLoopGroup> field_11553 = new Lazy<>(
		() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build())
	);
	public static final Lazy<EpollEventLoopGroup> field_11554 = new Lazy<>(
		() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build())
	);
	public static final Lazy<DefaultEventLoopGroup> field_11555 = new Lazy<>(
		() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build())
	);
	private final NetworkSide side;
	private final Queue<ClientConnection.PacketWrapper> packetQueue = Queues.newConcurrentLinkedQueue();
	private final ReentrantReadWriteLock field_11557 = new ReentrantReadWriteLock();
	private Channel channel;
	private SocketAddress address;
	private PacketListener packetListener;
	private Text disconnectReason;
	private boolean encrypted;
	private boolean disconnected;
	private int field_21500;
	private int field_21501;
	private float field_21502;
	private float field_21503;
	private int field_21504;
	private boolean field_21505;

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

	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
		if (throwable instanceof class_4375) {
			LOGGER.debug("Skipping packet due to errors", throwable.getCause());
		} else {
			boolean bl = !this.field_21505;
			this.field_21505 = true;
			if (this.channel.isOpen()) {
				if (throwable instanceof TimeoutException) {
					LOGGER.debug("Timeout", throwable);
					this.disconnect(new TranslatableText("disconnect.timeout"));
				} else {
					Text text = new TranslatableText("disconnect.genericReason", "Internal Exception: " + throwable);
					if (bl) {
						LOGGER.debug("Failed to sent packet", throwable);
						this.method_20160(new DisconnectS2CPacket(text), future -> this.disconnect(text));
						this.disableAutoRead();
					} else {
						LOGGER.debug("Double fault", throwable);
						this.disconnect(text);
					}
				}
			}
		}
	}

	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception {
		if (this.channel.isOpen()) {
			try {
				method_20159(packet, this.packetListener);
			} catch (OffThreadException var4) {
			}

			this.field_21500++;
		}
	}

	private static <T extends PacketListener> void method_20159(Packet<T> packet, PacketListener packetListener) {
		packet.apply((T)packetListener);
	}

	public void setPacketListener(PacketListener listener) {
		Validate.notNull(listener, "packetListener", new Object[0]);
		LOGGER.debug("Set listener of {} to {}", this, listener);
		this.packetListener = listener;
	}

	public void send(Packet<?> packet) {
		this.method_20160(packet, null);
	}

	public void method_20160(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		if (this.isOpen()) {
			this.sendQueuedPackets();
			this.method_20161(packet, genericFutureListener);
		} else {
			this.field_11557.writeLock().lock();

			try {
				this.packetQueue.add(new ClientConnection.PacketWrapper(packet, genericFutureListener));
			} finally {
				this.field_11557.writeLock().unlock();
			}
		}
	}

	private void method_20161(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		NetworkState networkState = NetworkState.getPacketHandlerState(packet);
		NetworkState networkState2 = (NetworkState)this.channel.attr(ATTR_KEY_PROTOCOL).get();
		this.field_21501++;
		if (networkState2 != networkState) {
			LOGGER.debug("Disabled auto read");
			this.channel.config().setAutoRead(false);
		}

		if (this.channel.eventLoop().inEventLoop()) {
			if (networkState != networkState2) {
				this.setState(networkState);
			}

			ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
			if (genericFutureListener != null) {
				channelFuture.addListener(genericFutureListener);
			}

			channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		} else {
			this.channel.eventLoop().execute(() -> {
				if (networkState != networkState2) {
					this.setState(networkState);
				}

				ChannelFuture channelFuturex = this.channel.writeAndFlush(packet);
				if (genericFutureListener != null) {
					channelFuturex.addListener(genericFutureListener);
				}

				channelFuturex.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
			});
		}
	}

	private void sendQueuedPackets() {
		if (this.channel != null && this.channel.isOpen()) {
			this.field_11557.readLock().lock();

			try {
				while (!this.packetQueue.isEmpty()) {
					ClientConnection.PacketWrapper packetWrapper = (ClientConnection.PacketWrapper)this.packetQueue.poll();
					this.method_20161(packetWrapper.packet, packetWrapper.field_21508);
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

		if (this.channel != null) {
			this.channel.flush();
		}

		if (this.field_21504++ % 20 == 0) {
			this.field_21503 = this.field_21503 * 0.75F + (float)this.field_21501 * 0.25F;
			this.field_21502 = this.field_21502 * 0.75F + (float)this.field_21500 * 0.25F;
			this.field_21501 = 0;
			this.field_21500 = 0;
		}
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

	@Nullable
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
					this.getPacketListener().onDisconnected(new TranslatableText("multiplayer.disconnect.generic"));
				}
			}
		}
	}

	public float method_20162() {
		return this.field_21502;
	}

	public float method_20163() {
		return this.field_21503;
	}

	static class PacketWrapper {
		private final Packet<?> packet;
		@Nullable
		private final GenericFutureListener<? extends Future<? super Void>> field_21508;

		public PacketWrapper(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
			this.packet = packet;
			this.field_21508 = genericFutureListener;
		}
	}
}
