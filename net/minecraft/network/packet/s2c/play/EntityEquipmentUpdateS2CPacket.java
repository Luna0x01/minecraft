package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityEquipmentUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int slot;
	private ItemStack stack;

	public EntityEquipmentUpdateS2CPacket() {
	}

	public EntityEquipmentUpdateS2CPacket(int i, int j, ItemStack itemStack) {
		this.id = i;
		this.slot = j;
		this.stack = itemStack == null ? null : itemStack.copy();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readVarInt();
		this.slot = buf.readShort();
		this.stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.id);
		buf.writeShort(this.slot);
		buf.writeItemStack(this.stack);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEquipmentUpdate(this);
	}

	public ItemStack getStack() {
		return this.stack;
	}

	public int getId() {
		return this.id;
	}

	public int getSlot() {
		return this.slot;
	}
}
