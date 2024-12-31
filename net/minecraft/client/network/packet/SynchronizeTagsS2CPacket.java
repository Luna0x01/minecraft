package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.util.PacketByteBuf;

public class SynchronizeTagsS2CPacket implements Packet<ClientPlayPacketListener> {
	private RegistryTagManager tagManager;

	public SynchronizeTagsS2CPacket() {
	}

	public SynchronizeTagsS2CPacket(RegistryTagManager registryTagManager) {
		this.tagManager = registryTagManager;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.tagManager = RegistryTagManager.fromPacket(packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		this.tagManager.toPacket(packetByteBuf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSynchronizeTags(this);
	}

	public RegistryTagManager getTagManager() {
		return this.tagManager;
	}
}
