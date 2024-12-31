package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ScreenHandlerSlotUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private int slot;
	private ItemStack stack = ItemStack.EMPTY;

	public ScreenHandlerSlotUpdateS2CPacket() {
	}

	public ScreenHandlerSlotUpdateS2CPacket(int syncId, int slot, ItemStack stack) {
		this.syncId = syncId;
		this.slot = slot;
		this.stack = stack.copy();
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScreenHandlerSlotUpdate(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.slot = buf.readShort();
		this.stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeShort(this.slot);
		buf.writeItemStack(this.stack);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getSlot() {
		return this.slot;
	}

	public ItemStack getItemStack() {
		return this.stack;
	}
}
