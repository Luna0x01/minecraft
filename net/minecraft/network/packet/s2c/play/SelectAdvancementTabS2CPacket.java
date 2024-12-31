package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class SelectAdvancementTabS2CPacket implements Packet<ClientPlayPacketListener> {
	@Nullable
	private Identifier tab;

	public SelectAdvancementTabS2CPacket() {
	}

	public SelectAdvancementTabS2CPacket(@Nullable Identifier identifier) {
		this.tab = identifier;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSelectAdvancementTab(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		if (buf.readBoolean()) {
			this.tab = buf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBoolean(this.tab != null);
		if (this.tab != null) {
			buf.writeIdentifier(this.tab);
		}
	}

	@Nullable
	public Identifier getTab() {
		return this.tab;
	}
}
