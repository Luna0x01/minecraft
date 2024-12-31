package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class CreativeInventoryActionC2SPacket implements Packet<ServerPlayPacketListener> {
	private int slot;
	private ItemStack stack;

	public CreativeInventoryActionC2SPacket() {
	}

	public CreativeInventoryActionC2SPacket(int i, ItemStack itemStack) {
		this.slot = i;
		this.stack = itemStack != null ? itemStack.copy() : null;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCreativeInventoryAction(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.slot = buf.readShort();
		this.stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeShort(this.slot);
		buf.writeItemStack(this.stack);
	}

	public int getSlot() {
		return this.slot;
	}

	public ItemStack getItemStack() {
		return this.stack;
	}
}
