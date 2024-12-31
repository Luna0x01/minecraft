package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.container.ContainerType;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class OpenContainerS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private int containerId;
	private Text name;

	public OpenContainerS2CPacket() {
	}

	public OpenContainerS2CPacket(int i, ContainerType<?> containerType, Text text) {
		this.syncId = i;
		this.containerId = Registry.CONTAINER.getRawId(containerType);
		this.name = text;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readVarInt();
		this.containerId = packetByteBuf.readVarInt();
		this.name = packetByteBuf.readText();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.syncId);
		packetByteBuf.writeVarInt(this.containerId);
		packetByteBuf.writeText(this.name);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOpenContainer(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	@Nullable
	public ContainerType<?> getContainerType() {
		return Registry.CONTAINER.get(this.containerId);
	}

	public Text getName() {
		return this.name;
	}
}
