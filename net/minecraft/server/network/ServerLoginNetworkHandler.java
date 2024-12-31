package net.minecraft.server.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
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
	private String field_8960 = "";
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
			this.disconnect("Took too long to log in");
		}
	}

	public void disconnect(String string) {
		try {
			LOGGER.info("Disconnecting " + this.getConnectionInfo() + ": " + string);
			LiteralText literalText = new LiteralText(string);
			this.connection.send(new LoginDisconnectS2CPacket(literalText));
			this.connection.disconnect(literalText);
		} catch (Exception var3) {
			LOGGER.error("Error whilst disconnecting player", var3);
		}
	}

	public void acceptPlayer() {
		if (!this.profile.isComplete()) {
			this.profile = this.toOfflineProfile(this.profile);
		}

		String string = this.server.getPlayerManager().checkCanJoin(this.connection.getAddress(), this.profile);
		if (string != null) {
			this.disconnect(string);
		} else {
			this.state = ServerLoginNetworkHandler.State.ACCEPTED;
			if (this.server.getNetworkCompressionThreshold() >= 0 && !this.connection.isLocal()) {
				this.connection.send(new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()), new ChannelFutureListener() {
					public void operationComplete(ChannelFuture channelFuture) throws Exception {
						ServerLoginNetworkHandler.this.connection.setCompressionThreshold(ServerLoginNetworkHandler.this.server.getNetworkCompressionThreshold());
					}
				});
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
		LOGGER.info(this.getConnectionInfo() + " lost connection: " + reason.asUnformattedString());
	}

	public String getConnectionInfo() {
		return this.profile != null ? this.profile.toString() + " (" + this.connection.getAddress().toString() + ")" : String.valueOf(this.connection.getAddress());
	}

	@Override
	public void onHello(LoginHelloC2SPacket packet) {
		Validate.validState(this.state == ServerLoginNetworkHandler.State.HELLO, "Unexpected hello packet", new Object[0]);
		this.profile = packet.getProfile();
		if (this.server.isOnlineMode() && !this.connection.isLocal()) {
			this.state = ServerLoginNetworkHandler.State.KEY;
			this.connection.send(new LoginHelloS2CPacket(this.field_8960, this.server.getKeyPair().getPublic(), this.nonce));
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
			(new Thread("User Authenticator #" + authenticatorThreadId.incrementAndGet()) {
					public void run() {
						GameProfile gameProfile = ServerLoginNetworkHandler.this.profile;

						try {
							String string = new BigInteger(
									NetworkEncryptionUtils.generateServerId(
										ServerLoginNetworkHandler.this.field_8960, ServerLoginNetworkHandler.this.server.getKeyPair().getPublic(), ServerLoginNetworkHandler.this.secretKey
									)
								)
								.toString(16);
							ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.server
								.getSessionService()
								.hasJoinedServer(new GameProfile(null, gameProfile.getName()), string);
							if (ServerLoginNetworkHandler.this.profile != null) {
								ServerLoginNetworkHandler.LOGGER
									.info("UUID of player " + ServerLoginNetworkHandler.this.profile.getName() + " is " + ServerLoginNetworkHandler.this.profile.getId());
								ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
							} else if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
								ServerLoginNetworkHandler.LOGGER.warn("Failed to verify username but will let them in anyway!");
								ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.toOfflineProfile(gameProfile);
								ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
							} else {
								ServerLoginNetworkHandler.this.disconnect("Failed to verify username!");
								ServerLoginNetworkHandler.LOGGER.error("Username '" + gameProfile.getName() + "' tried to join with an invalid session");
							}
						} catch (AuthenticationUnavailableException var3) {
							if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
								ServerLoginNetworkHandler.LOGGER.warn("Authentication servers are down but will let them in anyway!");
								ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.toOfflineProfile(gameProfile);
								ServerLoginNetworkHandler.this.state = ServerLoginNetworkHandler.State.READY_TO_ACCEPT;
							} else {
								ServerLoginNetworkHandler.this.disconnect("Authentication servers are down. Please try again later, sorry!");
								ServerLoginNetworkHandler.LOGGER.error("Couldn't verify username because servers are unavailable");
							}
						}
					}
				})
				.start();
		}
	}

	protected GameProfile toOfflineProfile(GameProfile profile) {
		UUID uUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + profile.getName()).getBytes(Charsets.UTF_8));
		return new GameProfile(uUID, profile.getName());
	}

	static enum State {
		HELLO,
		KEY,
		AUTHENTICATING,
		READY_TO_ACCEPT,
		DELAY_ACCEPT,
		ACCEPTED;
	}
}
