package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_4396 implements Packet<ServerLoginPacketListener> {
	private int field_21648;
	private PacketByteBuf field_21649;

	public class_4396() {
	}

	public class_4396(int i, @Nullable PacketByteBuf packetByteBuf) {
		this.field_21648 = i;
		this.field_21649 = packetByteBuf;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_21648 = buf.readVarInt();
		if (buf.readBoolean()) {
			int i = buf.readableBytes();
			if (i < 0 || i > 1048576) {
				throw new IOException("Payload may not be larger than 1048576 bytes");
			}

			this.field_21649 = new PacketByteBuf(buf.readBytes(i));
		} else {
			this.field_21649 = null;
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.field_21648);
		if (this.field_21649 != null) {
			buf.writeBoolean(true);
			buf.writeBytes(this.field_21649.copy());
		} else {
			buf.writeBoolean(false);
		}
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.method_20392(this);
	}
}
