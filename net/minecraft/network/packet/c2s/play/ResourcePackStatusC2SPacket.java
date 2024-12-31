package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ResourcePackStatusC2SPacket implements Packet<ServerPlayPacketListener> {
	private String shasum;
	private ResourcePackStatusC2SPacket.Status status;

	public ResourcePackStatusC2SPacket() {
	}

	public ResourcePackStatusC2SPacket(String string, ResourcePackStatusC2SPacket.Status status) {
		if (string.length() > 40) {
			string = string.substring(0, 40);
		}

		this.shasum = string;
		this.status = status;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.shasum = buf.readString(40);
		this.status = buf.readEnumConstant(ResourcePackStatusC2SPacket.Status.class);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.shasum);
		buf.writeEnumConstant(this.status);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onResourcePackStatus(this);
	}

	public static enum Status {
		SUCCESSFULLY_LOADED,
		DECLINED,
		FAILED_DOWNLOAD,
		ACCEPTED;
	}
}
