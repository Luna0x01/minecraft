package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import io.netty.channel.ChannelFutureListener;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.class_4325;
import net.minecraft.class_4396;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLoginNetworkHandler implements ServerLoginPacketListener, Tickable {
	private static final AtomicInteger authenticatorThreadId = new AtomicInteger(0);
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Random RANDOM = new Random();
	private final byte[] nonce = new byte[4];
	private final MinecraftServer server;
	public final ClientConnection connection;
	private ServerLoginNetworkHandler.State state = ServerLoginNetworkHandler.State.HELLO;
	private int loginTicks;
	private GameProfile profile;
	private final String field_8960 = "";
	private SecretKey secretKey;
	private ServerPlayerEntity clientEntity;

	public ServerLoginNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
		this.server = minecraftServer;
		this.connection = clientConnection;
		RANDOM.nextBytes(this.nonce);
	}

	@Override
	public void tick() {
		if (this.state == ServerLoginNetworkHandler.State.READY_TO_ACCEPT) {
			this.acceptPlayer();
		} else if (this.state == ServerLoginNetworkHandler.State.DELAY_ACCEPT) {
			ServerPlayerEntity serverPlayerEntity = this.server.getPlayerManager().getPlayer(this.profile.getId());
			if (serverPlayerEntity == null) {
				this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
				this.server.getPlayerManager().method_12827(this.connection, this.clientEntity);
				this.clientEntity = null;
			}
		}

		if (this.loginTicks++ == 600) {
			this.method_14978(new TranslatableText("multiplayer.disconnect.slow_login"));
		}
	}

	public void method_14978(Text text) {
		try {
			LOGGER.info("Disconnecting {}: {}", this.getConnectionInfo(), text.getString());
			this.connection.send(new LoginDisconnectS2CPacket(text));
			this.connection.disconnect(text);
		} catch (Exception var3) {
			LOGGER.error("Error whilst disconnecting player", var3);
		}
	}

	public void acceptPlayer() {
		if (!this.profile.isComplete()) {
			this.profile = this.method_21322(this.profile);
		}

		Text text = this.server.getPlayerManager().method_21386(this.connection.getAddress(), this.profile);
		if (text != null) {
			this.method_14978(text);
		} else {
			this.state = ServerLoginNetworkHandler.State.ACCEPTED;
			if (this.server.getNetworkCompressionThreshold() >= 0 && !this.connection.isLocal()) {
				this.connection
					.method_20160(
						new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()),
						(ChannelFutureListener)channelFuture -> this.connection.setCompressionThreshold(this.server.getNetworkCompressionThreshold())
					);
			}

			this.connection.send(new LoginSuccessS2CPacket(this.profile));
			ServerPlayerEntity serverPlayerEntity = this.server.getPlayerManager().getPlayer(this.profile.getId());
			if (serverPlayerEntity != null) {
				this.state = ServerLoginNetworkHandler.State.DELAY_ACCEPT;
				this.clientEntity = this.server.getPlayerManager().createPlayer(this.profile);
			} else {
				this.server.getPlayerManager().method_12827(this.connection, this.server.getPlayerManager().createPlayer(this.profile));
			}
		}
	}

	@Override
	public void onDisconnected(Text reason) {
		LOGGER.info("{} lost connection: {}", this.getConnectionInfo(), reason.getString());
	}

	public String getConnectionInfo() {
		return this.profile != null ? this.profile + " (" + this.connection.getAddress() + ")" : String.valueOf(this.connection.getAddress());
	}

	@Override
	public void onHello(LoginHelloC2SPacket packet) {
		Validate.validState(this.state == ServerLoginNetworkHandler.State.HELLO, "Unexpected hello packet", new Object[0]);
		this.profile = packet.getProfile();
		if (this.server.isOnlineMode() && !this.connection.isLocal()) {
			this.state = ServerLoginNetworkHandler.State.KEY;
			this.connection.send(new LoginHelloS2CPacket("", this.server.getKeyPair().getPublic(), this.nonce));
		} else {
			this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
		}
	}

	@Override
	public void onKey(LoginKeyC2SPacket packet) {
		Validate.validState(this.state == ServerLoginNetworkHandler.State.KEY, "Unexpected key packet", new Object[0]);
		PrivateKey privateKey = this.server.getKeyPair().getPrivate();
		if (!Arrays.equals(this.nonce, packet.decryptNonce(privateKey))) {
			throw new IllegalStateException("Invalid nonce!");
		} else {
			this.secretKey = packet.decryptSecretKey(privateKey);
			this.state = ServerLoginNetworkHandler.State.AUTHENTICATING;
			this.connection.setupEncryption(this.secretKey);
			Thread thread = new Thread("User Authenticator #" + authenticatorThreadId.incrementAndGet()) {
				public void run() {
					GameProfile gameProfile = ServerLoginNetworkHandler.this.profile;

					try {
						String string = new BigInteger(
								NetworkEncryptionUtils.generateServerId("", ServerLoginNetworkHandler.this.server.getKeyPair().getPublic(), ServerLoginNetworkHandler.this.secretKey)
							)
							.toString(16);
						ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.server
							.getSessionService()
							.hasJoinedServer(new GameProfile(null, gameProfile.getName()), string, this.method_13911());
						if (ServerLoginNetworkHandler.this.profile != null) {
							ServerLoginNetworkHandler.LOGGER
								.info("UUID of player {} is {}", ServerLoginNetworkHandler.this.profile.getName(), ServerLoginNetworkHandler.this.profile.getId());
							ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
						} else if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
							ServerLoginNetworkHandler.LOGGER.warn("Failed to verify username but will let them in anyway!");
							ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.method_21322(gameProfile);
							ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
						} else {
							ServerLoginNetworkHandler.this.method_14978(new TranslatableText("multiplayer.disconnect.unverified_username"));
							ServerLoginNetworkHandler.LOGGER.error("Username '{}' tried to join with an invalid session", gameProfile.getName());
						}
					} catch (AuthenticationUnavailableException var3) {
						if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
							ServerLoginNetworkHandler.LOGGER.warn("Authentication servers are down but will let them in anyway!");
							ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.method_21322(gameProfile);
							ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
						} else {
							ServerLoginNetworkHandler.this.method_14978(new TranslatableText("multiplayer.disconnect.authservers_down"));
							ServerLoginNetworkHandler.LOGGER.error("Couldn't verify username because servers are unavailable");
						}
					}
				}

				@Nullable
				private InetAddress method_13911() {
					SocketAddress socketAddress = ServerLoginNetworkHandler.this.connection.getAddress();
					return ServerLoginNetworkHandler.this.server.method_13912() && socketAddress instanceof InetSocketAddress
						? ((InetSocketAddress)socketAddress).getAddress()
						: null;
				}
			};
			thread.setUncaughtExceptionHandler(new class_4325(LOGGER));
			thread.start();
		}
	}

	@Override
	public void method_20392(class_4396 arg) {
		this.method_14978(new TranslatableText("multiplayer.disconnect.unexpected_query_response"));
	}

	protected GameProfile method_21322(GameProfile gameProfile) {
		UUID uUID = PlayerEntity.getOfflinePlayerUuid(gameProfile.getName());
		return new GameProfile(uUID, gameProfile.getName());
	}

	static enum State {
		HELLO,
		KEY,
		AUTHENTICATING,
		NEGOTIATING,
		READY_TO_ACCEPT,
		DELAY_ACCEPT,
		ACCEPTED;
	}
}
