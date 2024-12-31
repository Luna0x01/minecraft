package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class CooldownUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private Item item;
	private int cooldown;

	public CooldownUpdateS2CPacket() {
	}

	public CooldownUpdateS2CPacket(Item item, int i) {
		this.item = item;
		this.cooldown = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.item = Item.byRawId(packetByteBuf.readVarInt());
		this.cooldown = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(Item.getRawId(this.item));
		packetByteBuf.writeVarInt(this.cooldown);
	}

	public void method_11455(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCooldownUpdate(this);
	}

	public Item getItem() {
		return this.item;
	}

	public int getCooldown() {
		return this.cooldown;
	}
}
