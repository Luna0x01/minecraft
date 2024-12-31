package net.minecraft.client.network.packet;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginHelloS2CPacket implements Packet<ClientLoginPacketListener> {
	private String serverId;
	private PublicKey publicKey;
	private byte[] nonce;

	public LoginHelloS2CPacket() {
	}

	public LoginHelloS2CPacket(String string, PublicKey publicKey, byte[] bs) {
		this.serverId = string;
		this.publicKey = publicKey;
		this.nonce = bs;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.serverId = packetByteBuf.readString(20);
		this.publicKey = NetworkEncryptionUtils.readEncodedPublicKey(packetByteBuf.readByteArray());
		this.nonce = packetByteBuf.readByteArray();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.serverId);
		packetByteBuf.writeByteArray(this.publicKey.getEncoded());
		packetByteBuf.writeByteArray(this.nonce);
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onHello(this);
	}

	public String getServerId() {
		return this.serverId;
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	public byte[] getNonce() {
		return this.nonce;
	}
}
