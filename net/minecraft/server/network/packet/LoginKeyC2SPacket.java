package net.minecraft.server.network.packet;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginKeyC2SPacket implements Packet<ServerLoginPacketListener> {
	private byte[] encryptedSecretKey = new byte[0];
	private byte[] encryptedNonce = new byte[0];

	public LoginKeyC2SPacket() {
	}

	public LoginKeyC2SPacket(SecretKey secretKey, PublicKey publicKey, byte[] bs) {
		this.encryptedSecretKey = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
		this.encryptedNonce = NetworkEncryptionUtils.encrypt(publicKey, bs);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.encryptedSecretKey = packetByteBuf.readByteArray();
		this.encryptedNonce = packetByteBuf.readByteArray();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByteArray(this.encryptedSecretKey);
		packetByteBuf.writeByteArray(this.encryptedNonce);
	}

	public void method_12653(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onKey(this);
	}

	public SecretKey decryptSecretKey(PrivateKey privateKey) {
		return NetworkEncryptionUtils.decryptSecretKey(privateKey, this.encryptedSecretKey);
	}

	public byte[] decryptNonce(PrivateKey privateKey) {
		return privateKey == null ? this.encryptedNonce : NetworkEncryptionUtils.decrypt(privateKey, this.encryptedNonce);
	}
}
