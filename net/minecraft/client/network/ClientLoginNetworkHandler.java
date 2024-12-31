package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.class_4394;
import net.minecraft.class_4396;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsScreenProxy;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientLoginNetworkHandler implements ClientLoginPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	@Nullable
	private final Screen parent;
	private final Consumer<Text> field_20612;
	private final ClientConnection connection;
	private GameProfile profile;

	public ClientLoginNetworkHandler(ClientConnection clientConnection, MinecraftClient minecraftClient, @Nullable Screen screen, Consumer<Text> consumer) {
		this.connection = clientConnection;
		this.client = minecraftClient;
		this.parent = screen;
		this.field_20612 = consumer;
	}

	@Override
	public void onHello(LoginHelloS2CPacket packet) {
		SecretKey secretKey = NetworkEncryptionUtils.generateKey();
		PublicKey publicKey = packet.getPublicKey();
		String string = new BigInteger(NetworkEncryptionUtils.generateServerId(packet.getServerId(), publicKey, secretKey)).toString(16);
		LoginKeyC2SPacket loginKeyC2SPacket = new LoginKeyC2SPacket(secretKey, publicKey, packet.getNonce());
		this.field_20612.accept(new TranslatableText("connect.authorizing"));
		NetworkUtils.downloadExecutor.submit(() -> {
			Text text = this.method_18955(string);
			if (text != null) {
				if (this.client.getCurrentServerEntry() == null || !this.client.getCurrentServerEntry().isLocal()) {
					this.connection.disconnect(text);
					return;
				}

				LOGGER.warn(text.getString());
			}

			this.field_20612.accept(new TranslatableText("connect.encrypting"));
			this.connection.method_20160(loginKeyC2SPacket, future -> this.connection.setupEncryption(secretKey));
		});
	}

	@Nullable
	private Text method_18955(String string) {
		try {
			this.getSessionService().joinServer(this.client.getSession().getProfile(), this.client.getSession().getAccessToken(), string);
			return null;
		} catch (AuthenticationUnavailableException var3) {
			return new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.serversUnavailable"));
		} catch (InvalidCredentialsException var4) {
			return new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.invalidSession"));
		} catch (AuthenticationException var5) {
			return new TranslatableText("disconnect.loginFailedInfo", var5.getMessage());
		}
	}

	private MinecraftSessionService getSessionService() {
		return this.client.getSessionService();
	}

	@Override
	public void onLoginSuccess(LoginSuccessS2CPacket packet) {
		this.field_20612.accept(new TranslatableText("connect.joining"));
		this.profile = packet.getProfile();
		this.connection.setState(NetworkState.PLAY);
		this.connection.setPacketListener(new ClientPlayNetworkHandler(this.client, this.parent, this.connection, this.profile));
	}

	@Override
	public void onDisconnected(Text reason) {
		if (this.parent != null && this.parent instanceof RealmsScreenProxy) {
			this.client.setScreen(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.parent).getRealmsScreen(), "connect.failed", reason).getProxy());
		} else {
			this.client.setScreen(new DisconnectedScreen(this.parent, "connect.failed", reason));
		}
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

	@Override
	public void method_20383(class_4394 arg) {
		this.field_20612.accept(new TranslatableText("connect.negotiating"));
		this.connection.send(new class_4396(arg.method_20385(), null));
	}
}
