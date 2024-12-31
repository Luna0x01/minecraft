package net.minecraft.network.packet.s2c.login;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.listener.ClientLoginPacketListener;

public class LoginHelloS2CPacket implements Packet<ClientLoginPacketListener> {
	private String serverId;
	private byte[] publicKey;
	private byte[] nonce;

	public LoginHelloS2CPacket() {
	}

	public LoginHelloS2CPacket(String serverId, byte[] bs, byte[] nonce) {
		this.serverId = serverId;
		this.publicKey = bs;
		this.nonce = nonce;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.serverId = buf.readString(20);
		this.publicKey = buf.readByteArray();
		this.nonce = buf.readByteArray();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.serverId);
		buf.writeByteArray(this.publicKey);
		buf.writeByteArray(this.nonce);
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onHello(this);
	}

	public String getServerId() {
		return this.serverId;
	}

	public PublicKey getPublicKey() throws NetworkEncryptionException {
		return NetworkEncryptionUtils.readEncodedPublicKey(this.publicKey);
	}

	public byte[] getNonce() {
		return this.nonce;
	}
}
