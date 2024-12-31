package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.math.BigInteger;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientLoginNetworkHandler implements ClientLoginPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final Screen parent;
	private final ClientConnection connection;
	private GameProfile profile;

	public ClientLoginNetworkHandler(ClientConnection clientConnection, MinecraftClient minecraftClient, Screen screen) {
		this.connection = clientConnection;
		this.client = minecraftClient;
		this.parent = screen;
	}

	@Override
	public void onHello(LoginHelloS2CPacket packet) {
		final SecretKey secretKey = NetworkEncryptionUtils.generateKey();
		String string = packet.getServerId();
		PublicKey publicKey = packet.getPublicKey();
		String string2 = new BigInteger(NetworkEncryptionUtils.generateServerId(string, publicKey, secretKey)).toString(16);
		if (this.client.getCurrentServerEntry() != null && this.client.getCurrentServerEntry().isLocal()) {
			try {
				this.getSessionService().joinServer(this.client.getSession().getProfile(), this.client.getSession().getAccessToken(), string2);
			} catch (AuthenticationException var10) {
				LOGGER.warn("Couldn't connect to auth servers but will continue to join LAN");
			}
		} else {
			try {
				this.getSessionService().joinServer(this.client.getSession().getProfile(), this.client.getSession().getAccessToken(), string2);
			} catch (AuthenticationUnavailableException var7) {
				this.connection.disconnect(new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.serversUnavailable")));
				return;
			} catch (InvalidCredentialsException var8) {
				this.connection.disconnect(new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.invalidSession")));
				return;
			} catch (AuthenticationException var9) {
				this.connection.disconnect(new TranslatableText("disconnect.loginFailedInfo", var9.getMessage()));
				return;
			}
		}

		this.connection.send(new LoginKeyC2SPacket(secretKey, publicKey, packet.getNonce()), new GenericFutureListener<Future<? super Void>>() {
			public void operationComplete(Future<? super Void> future) throws Exception {
				ClientLoginNetworkHandler.this.connection.setupEncryption(secretKey);
			}
		});
	}

	private MinecraftSessionService getSessionService() {
		return this.client.getSessionService();
	}

	@Override
	public void onLoginSuccess(LoginSuccessS2CPacket packet) {
		this.profile = packet.getProfile();
		this.connection.setState(NetworkState.PLAY);
		this.connection.setPacketListener(new ClientPlayNetworkHandler(this.client, this.parent, this.connection, this.profile));
	}

	@Override
	public void onDisconnected(Text reason) {
		this.client.setScreen(new DisconnectedScreen(this.parent, "connect.failed", reason));
	}

	@Override
	public void onDisconnect(LoginDisconnectS2CPacket packet) {
		this.connection.disconnect(packet.getReason());
	}

	@Override
	public void onCompression(LoginCompressionS2CPacket packet) {
		if (!this.connection.isLocal()) {
			this.connection.setCompressionThreshold(packet.getCompressionThreshold());
		}
	}
}
